/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.codecs;

import org.bson.BSONWriter;
import org.mongodb.Encoder;

public class ShortArrayCodec implements Encoder<short[]> {
    private final ShortEncoder shortEncoder;

    public ShortArrayCodec() {
        shortEncoder = new ShortEncoder();
    }

    @Override
    public void encode(final BSONWriter bsonWriter, final short[] value) {
        bsonWriter.writeStartArray();
        for (short shortValue : value) {
            shortEncoder.encode(bsonWriter, shortValue);
        }
        bsonWriter.writeEndArray();
    }

    @Override
    public Class<short[]> getEncoderClass() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
