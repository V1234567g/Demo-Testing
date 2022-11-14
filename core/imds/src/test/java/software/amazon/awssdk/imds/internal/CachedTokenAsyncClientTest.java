/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.imds.internal;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.net.URI;
import java.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.imds.Ec2MetadataAsyncClient;
import software.amazon.awssdk.imds.MetadataResponse;
import software.amazon.awssdk.imds.TokenCacheStrategy;

public class CachedTokenAsyncClientTest {
    @Rule
    public WireMockRule mockMetadataEndpoint = new WireMockRule();

    private Ec2MetadataAsyncClient.Builder clientBuilder;

    @After
    public void reset() {
        mockMetadataEndpoint.resetAll();
    }

    @Before
    public void init() {
        this.clientBuilder = Ec2MetadataAsyncClient.builder()
                                                   .endpoint(URI.create("http://localhost:" + mockMetadataEndpoint.port()))
                                                   .tokenCacheStrategy(TokenCacheStrategy.BLOCKING);
    }
    @Test
    public void get_tokenFailsError4xx_shouldNotRetry() {
        stubFor(put(urlPathEqualTo("/latest/api/token")).willReturn(aResponse().withStatus(400).withBody("ERROR 400")));
        stubFor(get(urlPathEqualTo("/latest/meta-data/ami-id")).willReturn(aResponse().withBody("{}")));

        assertThatThrownBy(() -> clientBuilder.build().get("/latest/meta-data/ami-id").join())
            .getCause().isInstanceOf(SdkClientException.class);
        verify(exactly(1), putRequestedFor(urlPathEqualTo("/latest/api/token"))
            .withHeader("x-aws-ec2-metadata-token-ttl-seconds", equalTo("21600")));
    }

    @Test
    public void getToken_failsError5xx_shouldRetryUntilMaxRetriesIsReached() {
        stubFor(put(urlPathEqualTo("/latest/api/token")).willReturn(aResponse().withStatus(500).withBody("ERROR 500")));
        stubFor(get(urlPathEqualTo("/latest/meta-data/ami-id")).willReturn(aResponse().withBody("{}")));

        assertThatThrownBy(() -> clientBuilder.build().get("/latest/meta-data/ami-id").join())
            .getCause().isInstanceOf(SdkClientException.class);
        verify(exactly(4), putRequestedFor(urlPathEqualTo("/latest/api/token"))
            .withHeader("x-aws-ec2-metadata-token-ttl-seconds", equalTo("21600")));
    }

    @Test
    public void getToken_failsThenSucceeds_doesCacheTokenThatSucceeds() {
        stubFor(put(urlPathEqualTo("/latest/api/token")).inScenario("Retry Scenario")
                                                        .whenScenarioStateIs(STARTED)
                                                        .willReturn(aResponse().withStatus(500).withBody("Error 500"))
                                                        .willSetStateTo("Cause Success"));
        stubFor(put(urlPathEqualTo("/latest/api/token")).inScenario("Retry Scenario")
                                                        .whenScenarioStateIs("Cause Success")
                                                        .willReturn(aResponse().withBody("token-ok")));
        stubFor(get(urlPathEqualTo("/latest/meta-data/ami-id")).inScenario("Retry Scenario")
                                                               .whenScenarioStateIs("Cause Success")
                                                               .willReturn(aResponse().withBody("Success")));

        // 3 requests
        Ec2MetadataAsyncClient client = clientBuilder.build();
        MetadataResponse response = client.get("/latest/meta-data/ami-id").join();
        assertThat(response.asString()).isEqualTo("Success");
        response = client.get("/latest/meta-data/ami-id").join();
        assertThat(response.asString()).isEqualTo("Success");
        response = client.get("/latest/meta-data/ami-id").join();
        assertThat(response.asString()).isEqualTo("Success");

        verify(exactly(2), putRequestedFor(urlPathEqualTo("/latest/api/token"))
            .withHeader("x-aws-ec2-metadata-token-ttl-seconds", equalTo("21600")));
        verify(exactly(3), getRequestedFor(urlPathEqualTo("/latest/meta-data/ami-id"))
            .withHeader("x-aws-ec2-metadata-token", equalTo("token-ok")));
    }

    @Test
    public void get_multipleCallsSuccess_shouldReuseToken() throws Exception {
        stubFor(put(urlPathEqualTo("/latest/api/token")).willReturn(aResponse().withBody("some-token")));
        stubFor(get(urlPathEqualTo("/latest/meta-data/ami-id"))
                    .willReturn(aResponse().withBody("{}").withFixedDelay(1000)));

        int tokenTTlSeconds = 4;
        Ec2MetadataAsyncClient client = clientBuilder.tokenTtl(Duration.ofSeconds(tokenTTlSeconds)).build();

        int totalRequests = 10;
        for (int i = 0; i < totalRequests; i++) {
            MetadataResponse response = client.get("/latest/meta-data/ami-id").join();
            assertThat(response.asString()).isEqualTo("{}");
        }
        verify(exactly(3), putRequestedFor(urlPathEqualTo("/latest/api/token"))
            .withHeader("x-aws-ec2-metadata-token-ttl-seconds", equalTo(String.valueOf(tokenTTlSeconds))));
        verify(exactly(totalRequests), getRequestedFor(urlPathEqualTo("/latest/meta-data/ami-id"))
            .withHeader("x-aws-ec2-metadata-token", equalTo("some-token")));
    }

    @Test
    public void get_nonBlockingTokenCache_shouldReuseToken() throws Exception {
        stubFor(put(urlPathEqualTo("/latest/api/token")).willReturn(aResponse().withBody("some-token")));
        stubFor(get(urlPathEqualTo("/latest/meta-data/ami-id"))
                    .willReturn(aResponse().withBody("{}").withFixedDelay(1000)));

        int tokenTTlSeconds = 4;
        Ec2MetadataAsyncClient client = clientBuilder.tokenCacheStrategy(TokenCacheStrategy.NON_BLOCKING)
                                                .tokenTtl(Duration.ofSeconds(tokenTTlSeconds))
                                                .build();

        int totalRequests = 10;
        for (int i = 0; i < totalRequests; i++) {
            MetadataResponse response = client.get("/latest/meta-data/ami-id").join();
            assertThat(response.asString()).isEqualTo("{}");
        }
        verify(exactly(3), putRequestedFor(urlPathEqualTo("/latest/api/token"))
            .withHeader("x-aws-ec2-metadata-token-ttl-seconds", equalTo(String.valueOf(tokenTTlSeconds))));
        verify(exactly(totalRequests), getRequestedFor(urlPathEqualTo("/latest/meta-data/ami-id"))
            .withHeader("x-aws-ec2-metadata-token", equalTo("some-token")));
    }

}
