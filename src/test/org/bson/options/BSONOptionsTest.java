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

package org.bson.options;

import org.bson.UUIDRepresentation;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class BSONOptionsTest {
    @Test
    public void shouldUseSuppliedUUIDRepresentation() {
        // Given
        final UUIDRepresentation expectedUUIDRepresentation = UUIDRepresentation.STANDARD;

        // When
        final BSONOptions.Builder builder = new BSONOptions.Builder();
        builder.uuidRepresentation(expectedUUIDRepresentation);
        final BSONOptions bsonOptions = builder.build();

        // Then
        assertEquals(bsonOptions.getUUIDRepresentation(), expectedUUIDRepresentation);
    }

    @Test
    public void shouldDefaultToJavaUUIDRepresentation() {

        // When
        final BSONOptions bsonOptions = new BSONOptions.Builder().build();

        // Then
        assertEquals(bsonOptions.getUUIDRepresentation(), UUIDRepresentation.JAVA_LEGACY);
    }

}
