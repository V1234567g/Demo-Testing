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

package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Objects;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.dynamodb.model.ConsumedCapacity;
import software.amazon.awssdk.services.dynamodb.model.ItemCollectionMetrics;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

/**
 * Defines the elements returned by DynamoDB from a {@code PutItem} operation, such as
 * {@link DynamoDbTable#putItem(PutItemEnhancedRequest)} and {@link DynamoDbAsyncTable#putItem(PutItemEnhancedRequest)}.
 *
 * @param <T> The type of the item.
 */
@SdkPublicApi
@ThreadSafe
public final class PutItemEnhancedResponse<T> {
    private final T attributes;
    private final ConsumedCapacity consumedCapacity;
    private final ItemCollectionMetrics itemCollectionMetrics;

    private PutItemEnhancedResponse(Builder<T> builder) {
        this.attributes = builder.attributes;
        this.consumedCapacity = builder.consumedCapacity;
        this.itemCollectionMetrics = builder.itemCollectionMetrics;
    }

    /**
     * The attribute values as they appeared before the {@code PutItem} operation.
     */
    public T attributes() {
        return attributes;
    }

    /**
     * The capacity units consumed by the {@code PutItem} operation.
     *
     * @see PutItemResponse#consumedCapacity() for more information.
     */
    public ConsumedCapacity consumedCapacity() {
        return consumedCapacity;
    }

    /**
     * Information about item collections, if any, that were affected by the {@code PutItem} operation.
     *
     * @see PutItemResponse#itemCollectionMetrics() for more information.
     */
    public ItemCollectionMetrics itemCollectionMetrics() {
        return itemCollectionMetrics;
    }

    public static <T> Builder<T> builder(Class<? extends T> clzz) {
        return new Builder<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PutItemEnhancedResponse<?> that = (PutItemEnhancedResponse<?>) o;
        return Objects.equals(attributes, that.attributes)
               && Objects.equals(consumedCapacity, that.consumedCapacity)
               && Objects.equals(itemCollectionMetrics, that.itemCollectionMetrics);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(attributes);
        result = 31 * result + Objects.hashCode(consumedCapacity);
        result = 31 * result + Objects.hashCode(itemCollectionMetrics);
        return result;
    }

    @NotThreadSafe
    public static final class Builder<T> {
        private T attributes;
        private ConsumedCapacity consumedCapacity;
        private ItemCollectionMetrics itemCollectionMetrics;

        public Builder<T> attributes(T attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder<T> consumedCapacity(ConsumedCapacity consumedCapacity) {
            this.consumedCapacity = consumedCapacity;
            return this;
        }

        public Builder<T> itemCollectionMetrics(ItemCollectionMetrics itemCollectionMetrics) {
            this.itemCollectionMetrics = itemCollectionMetrics;
            return this;
        }

        public PutItemEnhancedResponse<T> build() {
            return new PutItemEnhancedResponse<>(this);
        }
    }
}
