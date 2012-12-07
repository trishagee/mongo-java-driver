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

package org.bson.translators;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

public class CSharpUUIDTranslatorTest {
    @Test
    public void shouldEncodeLong() throws IOException {
        // Given
        final CSharpUUIDTranslator longTranslator = new CSharpUUIDTranslator();

        // When
        final byte[] actualBytes = longTranslator.toBytes(new UUID(2L, 1L));

        // Then
        final byte[] expectedBytes = {0, 0, 0, 0, 0, 0, 2, 0,
                                      0, 0, 0, 0, 0, 0, 0, 1}; // Not really sure what's going on here....
        assertEquals(actualBytes, expectedBytes);
    }

    @Test
    public void shouldReadEncodedLongs() {
        // Given
        final CSharpUUIDTranslator longTranslator = new CSharpUUIDTranslator();

        // When
        final byte[] bytesToRead = {0, 0, 0, 0, 0, 0, 2, 0,
                                    0, 0, 0, 0, 0, 0, 0, 1}; // Not really sure what's going on here....
        final UUID actualUUID = longTranslator.fromBytes(bytesToRead);

        // Then
        final UUID expectedUUID = new UUID(2L, 1L);
        assertEquals(actualUUID, expectedUUID);
    }
}
