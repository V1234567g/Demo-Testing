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

package software.amazon.awssdk.enhanced.dynamodb.internal.converter.string;

import java.util.Locale;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.StringConverter;

/**
 * A converter between {@link Locale} and {@link String}.
 */
@SdkInternalApi
@ThreadSafe
@Immutable
public class LocaleStringConverter implements StringConverter<Locale> {
    private LocaleStringConverter() {
    }

    public static LocaleStringConverter create() {
        return new LocaleStringConverter();
    }

    @Override
    public EnhancedType<Locale> type() {
        return EnhancedType.of(Locale.class);
    }

    @Override
    public Locale fromString(String string) {
        return Locale.forLanguageTag(string);
    }

    @Override
    public String toString(Locale locale) {
        return locale.toLanguageTag();
    }

}
