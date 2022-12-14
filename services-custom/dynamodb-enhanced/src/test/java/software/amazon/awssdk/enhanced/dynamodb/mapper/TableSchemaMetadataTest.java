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

package software.amazon.awssdk.enhanced.dynamodb.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.atomicCounter;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.updateBehavior;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.extensions.AutoGeneratedTimestampRecordExtension;
import software.amazon.awssdk.enhanced.dynamodb.internal.mapper.AtomicCounter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.testbeans.CustomMetadataBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.testbeans.CustomMetadataImmutableBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.testbeans.CustomMetadataItem;

public class TableSchemaMetadataTest {

    private static List<TableSchema> tableSchemas;

    @BeforeAll
    static void initializeSchemas() {
        StaticTableSchema<CustomMetadataItem> staticTableSchema =
            StaticTableSchema.builder(CustomMetadataItem.class)
                             .newItemSupplier(CustomMetadataItem::new)
                             .addAttribute(String.class, a -> a.name("id")
                                                               .getter(CustomMetadataItem::getId)
                                                               .setter(CustomMetadataItem::setId)
                                                               .addTag(primaryPartitionKey()))
                             .addAttribute(String.class, a -> a.name("customMetadataObjectAttribute")
                                                                .getter(CustomMetadataItem::getCustomMetadataObjectAttribute)
                                                                .setter(CustomMetadataItem::setCustomMetadataObjectAttribute)
                                                                .addTag(updateBehavior(UpdateBehavior.WRITE_IF_NOT_EXISTS)))
                             .addAttribute(Instant.class, a -> a.name("customMetadataCollectionAttribute")
                                                                .getter(CustomMetadataItem::getCustomMetadataCollectionAttribute)
                                                                .setter(CustomMetadataItem::setCustomMetadataCollectionAttribute)
                                                                .addTag(getTimestampTag()))
                             .addAttribute(Long.class, a -> a.name("customMetadataMapAttribute")
                                                                .getter(CustomMetadataItem::getCustomMetadataMapAttribute)
                                                                .setter(CustomMetadataItem::setCustomMetadataMapAttribute)
                                                                .addTag(atomicCounter()))
                             .build();
        tableSchemas = new ArrayList<>();
        tableSchemas.add(BeanTableSchema.create(CustomMetadataBean.class));
        tableSchemas.add(ImmutableTableSchema.create(CustomMetadataImmutableBean.class));
        tableSchemas.add(staticTableSchema);
    }

    @ParameterizedTest
    @MethodSource("tableSchemas")
    void testCustomMetadataObjectTags(TableSchema tableSchema) {
        Optional<UpdateBehavior> customMetadataObject =
            tableSchema.tableMetadata().customMetadataObject("UpdateBehavior:customMetadataObjectAttribute", UpdateBehavior.class);
        assertThat(customMetadataObject).isPresent();
        UpdateBehavior updateBehavior = customMetadataObject.get();
        assertThat(updateBehavior).isEqualTo(UpdateBehavior.WRITE_IF_NOT_EXISTS);
    }

    @ParameterizedTest
    @MethodSource("tableSchemas")
    void testCustomMetadataCollectionTags(TableSchema tableSchema) {
        Optional<Collection> customMetadataCollection =
            tableSchema.tableMetadata().customMetadataObject("AutoGeneratedTimestampExtension:AutoGeneratedTimestampAttribute",
                                                             Collection.class);
        assertThat(customMetadataCollection).isPresent();
        Collection<String> timestamps = customMetadataCollection.get();
        assertThat(timestamps).hasSize(1);
        String timestamp = timestamps.iterator().next();
        assertThat(timestamp).isNotNull();
        assertThat(timestamp).isEqualTo("customMetadataCollectionAttribute");
    }

    @ParameterizedTest
    @MethodSource("tableSchemas")
    void testCustomMetadataMapTags(TableSchema tableSchema) {
        Optional<Map> customMetadataMap = tableSchema.tableMetadata()
                                                     .customMetadataObject("AtomicCounter:Counters", Map.class);
        assertThat(customMetadataMap).isPresent();
        Map<String, AtomicCounter> atomicCounters = customMetadataMap.get();
        AtomicCounter customMetadataMapAttribute = atomicCounters.get("customMetadataMapAttribute");
        assertThat(customMetadataMapAttribute).isNotNull();
        assertThat(customMetadataMapAttribute.startValue().value()).isEqualTo(0L);
        assertThat(customMetadataMapAttribute.delta().value()).isEqualTo(1L);
    }

    static Stream<TableSchema> tableSchemas() {
        return tableSchemas.stream();
    }

    static StaticAttributeTag getTimestampTag() {
        return AutoGeneratedTimestampRecordExtension.AttributeTags.autoGeneratedTimestampAttribute();
    }

}
