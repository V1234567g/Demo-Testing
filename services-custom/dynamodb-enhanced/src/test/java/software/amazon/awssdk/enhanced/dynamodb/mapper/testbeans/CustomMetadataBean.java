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

package software.amazon.awssdk.enhanced.dynamodb.mapper.testbeans;

import java.time.Instant;
import java.util.Objects;
import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbAtomicCounter;
import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbAutoGeneratedTimestampAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.UpdateBehavior;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbUpdateBehavior;

@DynamoDbBean
public class CustomMetadataBean {
    private String id;
    private String customMetadataObjectAttribute;
    private Instant customMetadataCollectionAttribute;
    private Long customMetadataMapAttribute;

    @DynamoDbPartitionKey
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbUpdateBehavior(UpdateBehavior.WRITE_IF_NOT_EXISTS)
    public String getCustomMetadataObjectAttribute() {
        return customMetadataObjectAttribute;
    }

    public void setCustomMetadataObjectAttribute(String customMetadataObjectAttribute) {
        this.customMetadataObjectAttribute = customMetadataObjectAttribute;
    }

    @DynamoDbAutoGeneratedTimestampAttribute
    public Instant getCustomMetadataCollectionAttribute() {
        return customMetadataCollectionAttribute;
    }

    public void setCustomMetadataCollectionAttribute(Instant customMetadataCollectionAttribute) {
        this.customMetadataCollectionAttribute = customMetadataCollectionAttribute;
    }

    @DynamoDbAtomicCounter
    public Long getCustomMetadataMapAttribute() {
        return customMetadataMapAttribute;
    }

    public void setCustomMetadataMapAttribute(Long customMetadataMapAttribute) {
        this.customMetadataMapAttribute = customMetadataMapAttribute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomMetadataBean that = (CustomMetadataBean) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(customMetadataObjectAttribute, that.customMetadataObjectAttribute) &&
               Objects.equals(customMetadataCollectionAttribute, that.customMetadataCollectionAttribute) &&
               Objects.equals(customMetadataMapAttribute, that.customMetadataMapAttribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customMetadataObjectAttribute, customMetadataCollectionAttribute, customMetadataMapAttribute);
    }
}
