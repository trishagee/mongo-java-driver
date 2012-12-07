/*
 * Copyright (c) 2008 - 2012 10gen, Inc. <http://10gen.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.bson;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.UUID;

import static org.bson.UUIDRepresentation.STANDARD;

public class BasicBSONDecoderTest {

    @Test
    public void shouldUseTheJavaLegacyRepresentationToDecodeUUIDsByDefault() throws IOException {
        // Given
        final BasicBSONDecoder bsonDecoder = new BasicBSONDecoder();

        final UUID expectedUUID = new UUID(2, 1);

        final byte[] binaryTypeWithUUIDAsBytes = {
                31, 0, 0, 0,            // message length
                5,                      // type (BINARY)
                95, 105, 100, 0,        // "_id"
                16, 0, 0, 0,            // int "16" (length)
                4,                      // type (B_UUID_STANDARD)
                2, 0, 0, 0, 0, 0, 0, 0, //
                1, 0, 0, 0, 0, 0, 0, 0, // 8 bytes for long, 2 longs for UUID, Little Endian for Default (Java) encoding
                0};                     // EOM

        // When
        final BasicBSONCallback callback = new BasicBSONCallback();
        bsonDecoder.decode(binaryTypeWithUUIDAsBytes, callback);

        // Then
        Assert.assertEquals(((BSONObject) callback.get()).get("_id"), expectedUUID);
    }

    @Test
    public void shouldUseTheProvidedUUIDRepresentationToDecodeUUIDs() throws IOException {
        // Given
        final BSONOptions bsonOptions = new BSONOptions.Builder().uuidRepresentation(STANDARD).build();
        final BasicBSONDecoder bsonDecoder = new BasicBSONDecoder(bsonOptions);

        final UUID expectedUUID = new UUID(2, 1);

        final byte[] binaryTypeWithUUIDAsBytes = {
                31, 0, 0, 0,            // message length
                5,                      // type (BINARY)
                95, 105, 100, 0,        // "_id"
                16, 0, 0, 0,            // int "16" (length)
                4,                      // type (B_UUID_STANDARD)
                0, 0, 0, 0, 0, 0, 0, 2,
                0, 0, 0, 0, 0, 0, 0, 1, // 8 bytes for long, 2 longs for UUID, Big Endian for Standard encoding
                0};                     // EOM

        // When
        final BasicBSONCallback callback = new BasicBSONCallback();
        bsonDecoder.decode(binaryTypeWithUUIDAsBytes, callback);

        // Then
        Assert.assertEquals(((BSONObject) callback.get()).get("_id"), expectedUUID);
    }
}
