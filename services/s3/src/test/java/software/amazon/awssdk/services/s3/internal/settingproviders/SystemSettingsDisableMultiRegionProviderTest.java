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

package software.amazon.awssdk.services.s3.internal.settingproviders;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static software.amazon.awssdk.services.s3.S3SystemSetting.AWS_S3_DISABLE_MULTIREGION_ACCESS_POINTS;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.testutils.EnvironmentVariableHelper;

public class SystemSettingsDisableMultiRegionProviderTest {
    private final SystemsSettingsDisableMultiRegionProvider provider = SystemsSettingsDisableMultiRegionProvider.create();
    private final EnvironmentVariableHelper helper = new EnvironmentVariableHelper();

    @AfterEach
    public void clearSystemProperty() {
        System.clearProperty(AWS_S3_DISABLE_MULTIREGION_ACCESS_POINTS.property());
        helper.reset();
    }

    @Test
    public void notSpecified_shouldReturnEmptyOptional() {
        assertThat(provider.resolve()).isEqualTo(Optional.empty());
    }

    @Test
    public void specifiedInSystemProperties_shouldResolve() {
        System.setProperty(AWS_S3_DISABLE_MULTIREGION_ACCESS_POINTS.property(), "false");
        assertThat(provider.resolve()).isEqualTo(Optional.of(FALSE));
    }

    @Test
    public void specifiedInEnvironmentVariables_shouldResolve() {
        helper.set(AWS_S3_DISABLE_MULTIREGION_ACCESS_POINTS.environmentVariable(), "true");
        assertThat(provider.resolve()).isEqualTo(Optional.of(TRUE));
    }

    @Test
    public void specifiedInBothPlaces_SystemPropertiesShouldTakePrecedence() {
        System.setProperty(AWS_S3_DISABLE_MULTIREGION_ACCESS_POINTS.property(), "true");
        helper.set(AWS_S3_DISABLE_MULTIREGION_ACCESS_POINTS.environmentVariable(), "false");
        assertThat(provider.resolve()).isEqualTo(Optional.of(TRUE));
    }

    @Test
    public void mixedSpace_shouldResolveCorrectly() {
        System.setProperty(AWS_S3_DISABLE_MULTIREGION_ACCESS_POINTS.property(), "tRuE");
        assertThat(provider.resolve()).isEqualTo(Optional.of(TRUE));
    }

    @Test
    public void emptySystemProperties_shouldThrowError() {
        System.setProperty(AWS_S3_DISABLE_MULTIREGION_ACCESS_POINTS.property(), "");
        assertThatThrownBy(provider::resolve).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nonBoolean_shouldThrowError() {
        System.setProperty(AWS_S3_DISABLE_MULTIREGION_ACCESS_POINTS.property(), "enable");
        assertThatThrownBy(provider::resolve).isInstanceOf(IllegalStateException.class);
    }
}
