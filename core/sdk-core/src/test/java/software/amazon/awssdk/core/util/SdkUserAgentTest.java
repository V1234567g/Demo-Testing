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

package software.amazon.awssdk.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.utils.JavaSystemSetting;

public class SdkUserAgentTest {

    @Test
    public void userAgent() {
        String userAgent = SdkUserAgent.create().userAgent();
        assertNotNull(userAgent);
        Arrays.stream(userAgent.split(" ")).forEach(str -> assertThat(isValidInput(str)).isTrue());
    }

    @Test
    public void userAgent_HasVendor() {
        System.setProperty(JavaSystemSetting.JAVA_VENDOR.property(), "finks");
        String userAgent = SdkUserAgent.create().getUserAgent();
        System.clearProperty(JavaSystemSetting.JAVA_VENDOR.property());
        assertThat(userAgent).contains("vendor/finks");
    }

    @Test
    public void userAgent_HasUnknownVendor() {
        System.clearProperty(JavaSystemSetting.JAVA_VENDOR.property());
        String userAgent = SdkUserAgent.create().getUserAgent();
        assertThat(userAgent).contains("vendor/unknown");
    }

    private boolean isValidInput(String input) {
        return input.startsWith("(") || input.contains("/") && input.split("/").length == 2;
    }
}
