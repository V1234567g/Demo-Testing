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

package software.amazon.awssdk.auth.token.signer;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;

/**
 * SdkToken authorizing attributes attached to the execution.
 */
@SdkProtectedApi
public final class SdkTokenExecutionAttribute {

    /**
     * The token to sign requests using token authorization instead of AWS Credentials.
     */
    public static final ExecutionAttribute<SdkToken> SDK_TOKEN = new ExecutionAttribute<>("SdkToken");

    private SdkTokenExecutionAttribute() {
    }
}
