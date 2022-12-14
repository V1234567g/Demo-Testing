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

package software.amazon.awssdk.services.dynamodb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.paginators.ScanIterable;
import software.amazon.awssdk.services.dynamodb.paginators.ScanPublisher;
import utils.resources.tables.BasicTempTable;
import utils.test.util.DynamoDBTestBase;
import utils.test.util.TableUtils;

public class PaginatorIntegrationTest extends DynamoDBTestBase {

    private static final String TABLE_NAME = "java-sdk-scan-paginator-test" + System.currentTimeMillis();
    private static final String HASH_KEY_NAME = BasicTempTable.HASH_KEY_NAME;
    private static final String ATTRIBUTE_FOO = "attribute_foo";
    private static final int ITEM_COUNT = 19;

    @BeforeClass
    public static void setUpFixture() throws Exception {
        DynamoDBTestBase.setUpTestBase();

        dynamo.createTable(CreateTableRequest.builder().tableName(TABLE_NAME)
                                             .keySchema(KeySchemaElement.builder().keyType(KeyType.HASH)
                                                                        .attributeName(HASH_KEY_NAME)
                                                                        .build())
                                             .attributeDefinitions(AttributeDefinition.builder()
                                                                                      .attributeType(ScalarAttributeType.N)
                                                                                      .attributeName(HASH_KEY_NAME)
                                                                                      .build())
                                             .provisionedThroughput(ProvisionedThroughput.builder()
                                                                                         .readCapacityUnits(5L)
                                                                                         .writeCapacityUnits(5L)
                                                                                         .build())
                                             .build());

        TableUtils.waitUntilActive(dynamo, TABLE_NAME);

        putTestData();
    }

    @AfterClass
    public static void cleanUpFixture() {
        dynamo.deleteTable(DeleteTableRequest.builder().tableName(TABLE_NAME).build());
    }

    @Test
    public void batchGetItem_allProcessed_shouldNotHaveNextPage() {
        Map<String, KeysAndAttributes> attributes = new HashMap<>();
        Map<String, AttributeValue> keys;
        KeysAndAttributes keysAndAttributes;
        List<Map<String, AttributeValue>> keysList = new ArrayList<>();

        for (int i = 0; i < ITEM_COUNT; i++) {
            keys = new HashMap<>();
            keys.put(HASH_KEY_NAME, AttributeValue.builder().n(String.valueOf(i)).build());
            keysList.add(keys);
        }

        keysAndAttributes = KeysAndAttributes.builder().keys(keysList).build();

        attributes.put(TABLE_NAME, keysAndAttributes);

        Iterator<BatchGetItemResponse> iterator =
            dynamo.batchGetItemPaginator(b -> b.requestItems(attributes)).iterator();
        assertThat(iterator.next().unprocessedKeys().isEmpty()).isTrue();
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    public void test_MultipleIteration_On_Responses_Iterable() {
        ScanRequest request = scanRequest(2);
        ScanIterable scanResponses = dynamo.scanPaginator(request);

        int count = 0;

        // Iterate once
        for (ScanResponse response : scanResponses) {
            count += response.count();
        }
        assertEquals(ITEM_COUNT, count);

        // Iterate second time
        count = 0;
        for (ScanResponse response : scanResponses) {
            count += response.count();
        }
        assertEquals(ITEM_COUNT, count);
    }

    @Test
    public void test_MultipleIteration_On_PaginatedMember_Iterable() {
        ScanRequest request = scanRequest(2);
        SdkIterable<Map<String, AttributeValue>> items = dynamo.scanPaginator(request).items();

        int count = 0;

        // Iterate once
        for (Map<String, AttributeValue> item : items) {
            count++;
        }
        assertEquals(ITEM_COUNT, count);

        // Iterate second time
        count = 0;
        for (Map<String, AttributeValue> item : items) {
            count++;
        }
        assertEquals(ITEM_COUNT, count);
    }

    @Test
    public void test_MultipleIteration_On_Responses_Stream() {
        int results_per_page = 2;
        ScanRequest request = scanRequest(results_per_page);
        ScanIterable scanResponses = dynamo.scanPaginator(request);

        // Iterate once
        assertEquals(ITEM_COUNT, scanResponses.stream()
                                              .mapToInt(response -> response.count())
                                              .sum());

        // Iterate second time
        assertEquals(ITEM_COUNT, scanResponses.stream()
                                              .mapToInt(response -> response.count())
                                              .sum());

        // Number of pages
        assertEquals(Math.ceil((double) ITEM_COUNT / results_per_page), scanResponses.stream().count(), 0);
    }

    @Test
    public void test_MultipleIteration_On_PaginatedMember_Stream() {
        ScanRequest request = scanRequest(2);
        SdkIterable<Map<String, AttributeValue>> items = dynamo.scanPaginator(request).items();

        // Iterate once
        assertEquals(ITEM_COUNT, items.stream().distinct().count());

        // Iterate second time
        assertEquals(ITEM_COUNT, items.stream().distinct().count());
    }

    @Test(expected = IllegalStateException.class)
    public void iteration_On_SameStream_ThrowsError() {
        int results_per_page = 2;
        ScanRequest request = ScanRequest.builder().tableName(TABLE_NAME).limit(results_per_page).build();
        Stream<Map<String, AttributeValue>> itemsStream = dynamo.scanPaginator(request).items().stream();

        // Iterate once
        assertEquals(ITEM_COUNT, itemsStream.distinct().count());

        // Iterate second time
        assertEquals(ITEM_COUNT, itemsStream.distinct().count());
    }

    @Test
    public void mix_Iterator_And_Stream_Calls() {
        ScanRequest request = scanRequest(2);
        ScanIterable scanResponses = dynamo.scanPaginator(request);

        assertEquals(ITEM_COUNT, scanResponses.stream().flatMap(r -> r.items().stream())
                                                     .distinct()
                                                     .count());


        assertEquals(ITEM_COUNT, scanResponses.stream().mapToInt(response -> response.count()).sum());


        int count = 0;
        for (ScanResponse response : scanResponses) {
            count += response.count();
        }
        assertEquals(ITEM_COUNT, count);
    }
    
    @Test
    public void sdkPublisher_subscribe_handlesExceptions() throws Exception {
        RuntimeException innerException = new RuntimeException();
        ScanRequest request = scanRequest(2);
        try {
            dynamoAsync.scanPaginator(request).subscribe(r -> {
                throw innerException;
            }).get(5, TimeUnit.SECONDS);
        } catch (ExecutionException executionException) {
            assertThat(executionException.getCause()).isSameAs(innerException);
        }
    }

    @Test
    public void sdkPublisher_filter_handlesExceptions()  {
        sdkPublisherFunctionHandlesException((p, t) -> p.filter(f -> { throw t; }));
    }

    @Test
    public void sdkPublisher_map_handlesExceptions() {
        sdkPublisherFunctionHandlesException((p, t) -> p.map(f -> { throw t; }));
    }

    @Test
    public void sdkPublisher_flatMapIterable_handlesExceptions() {
        sdkPublisherFunctionHandlesException((p, t) -> p.flatMapIterable(f -> { throw t; }));
    }

    public void sdkPublisherFunctionHandlesException(BiFunction<ScanPublisher, RuntimeException, SdkPublisher<?>> function) {
        RuntimeException innerException = new RuntimeException();
        ScanRequest request = scanRequest(2);
        try {
            function.apply(dynamoAsync.scanPaginator(request), innerException).subscribe(r -> {}).get(5, TimeUnit.SECONDS);
        } catch (ExecutionException executionException) {
            assertThat(executionException.getCause()).isSameAs(innerException);
        } catch (InterruptedException | TimeoutException e) {
            throw new AssertionError("SDK Publisher function did not handle exceptions correctly.", e);
        }
    }

    private static void putTestData() {
        Map<String, AttributeValue> item = new HashMap();
        Random random = new Random();

        for (int hashKeyValue = 0; hashKeyValue < ITEM_COUNT; hashKeyValue++) {
            item.put(HASH_KEY_NAME, AttributeValue.builder().n(Integer.toString(hashKeyValue)).build());
            item.put(ATTRIBUTE_FOO, AttributeValue.builder().n(Integer.toString(random.nextInt(ITEM_COUNT))).build());

            dynamo.putItem(PutItemRequest.builder().tableName(TABLE_NAME).item(item).build());
            item.clear();
        }
    }

    private ScanRequest scanRequest(int limit) {
        return ScanRequest.builder().tableName(TABLE_NAME).consistentRead(true).limit(limit).build();
    }
}
