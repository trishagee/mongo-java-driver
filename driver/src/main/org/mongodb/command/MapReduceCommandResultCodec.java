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

package org.mongodb.command;

import org.bson.BSONReader;
import org.bson.BSONType;
import org.mongodb.Decoder;
import org.mongodb.codecs.BSONCodecs;
import org.mongodb.codecs.DocumentCodec;

import java.util.ArrayList;
import java.util.List;

public class MapReduceCommandResultCodec<T> extends DocumentCodec {

    private final Decoder<T> decoder;

    public MapReduceCommandResultCodec(final BSONCodecs bsonCodecs, final Decoder<T> decoder) {
        super(bsonCodecs);
        this.decoder = decoder;
    }

    @Override
    protected Object readValue(final BSONReader reader, final String fieldName) {
        if ("results".equals(fieldName)) {
            return readArray(reader);
        } else {
            return super.readValue(reader, fieldName);
        }
    }

    private List<T> readArray(final BSONReader reader) {
        final List<T> list = new ArrayList<T>();
        reader.readStartArray();
        while (reader.readBSONType() != BSONType.END_OF_DOCUMENT) {
            list.add(decoder.decode(reader));
        }
        reader.readEndArray();
        return list;
    }
}
