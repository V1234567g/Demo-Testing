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

package software.amazon.awssdk.core.internal.io;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
class ChunkContentIterator {

    private final byte[] bytes;
    private int pos;

    ChunkContentIterator(byte[] bytes) {
        this.bytes = bytes;
    }

    public boolean hasNext() {
        return pos < bytes.length;
    }

    public int read(byte[] output, int offset, int length) {
        if (length == 0) {
            return 0;
        }
        if (!hasNext()) {
            return -1;
        }
        int remaingBytesNum = bytes.length - pos;
        int bytesToRead = Math.min(remaingBytesNum, length);
        System.arraycopy(bytes, pos, output, offset, bytesToRead);
        pos += bytesToRead;
        return bytesToRead;
    }
}
