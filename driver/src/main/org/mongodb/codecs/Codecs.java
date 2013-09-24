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

import org.bson.BSONReader;
import org.bson.BSONType;
import org.bson.BSONWriter;
import org.mongodb.Codec;
import org.mongodb.Encoder;
import org.mongodb.codecs.validators.QueryFieldNameValidator;
import org.mongodb.codecs.validators.Validator;

import java.util.Map;

import static java.lang.String.format;

public class Codecs implements Codec<Object> {
    private final BSONCodecs bsonCodecs;
    private final EncoderRegistry encoderRegistry;
    private final IterableCodec iterableCodec;
    private final MapCodec mapCodec;

    public Codecs(final BSONCodecs bsonCodecs, final EncoderRegistry encoderRegistry) {
        //defaulting to the less rigorous, and maybe more common, validation - lets through $, dots etc.
        this(bsonCodecs, new QueryFieldNameValidator(), encoderRegistry);
    }

    public Codecs(final BSONCodecs bsonCodecs,
                  final Validator<String> fieldNameValidator,
                  final EncoderRegistry encoderRegistry) {
        this.bsonCodecs = bsonCodecs;
        this.encoderRegistry = encoderRegistry;
        iterableCodec = new IterableCodec(bsonCodecs);
        mapCodec = new MapCodec(this, fieldNameValidator);
    }

    public static Codecs createDefault() {
        return builder().primitiveCodecs(BSONCodecs.createDefault()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"}) // going to have some unchecked warnings because of all the casting from Object
    public void encode(final BSONWriter bsonWriter, final Object object) {
        if (object == null || bsonCodecs.canEncode(object.getClass())) {
            bsonCodecs.encode(bsonWriter, object);
        }
        else if (encoderRegistry.get(object.getClass()) != null) {
            final Encoder<Object> codec = (Encoder<Object>) encoderRegistry.get(object.getClass());
            codec.encode(bsonWriter, object);
        } else if (object instanceof Map) {
            encode(bsonWriter, (Map) object);
        }
        else {
            encoderRegistry.getDefaultEncoder().encode(bsonWriter, object);
        }
    }

    @Override
    public Class<Object> getEncoderClass() {
        return Object.class;
    }

    public void encode(final BSONWriter bsonWriter, final Iterable<?> value) {
        iterableCodec.encode(bsonWriter, value);
    }

    public void encode(final BSONWriter bsonWriter, final Map<String, Object> value) {
        mapCodec.encode(bsonWriter, value);
    }

    public static Builder builder() {
        return new Builder();
    }

    //TODO: don't like this at all.  Feels like if it has a BSON type, it's a primitive
    public Object decode(final BSONReader reader) {
        if (bsonCodecs.canDecodeNextObject(reader)) {
            return bsonCodecs.decode(reader);
        } else if (reader.getCurrentBSONType() == BSONType.ARRAY) {
            return iterableCodec.decode(reader);
        } else {
            throw new UnsupportedOperationException(format("The BSON type %s does not have a decoder associated with it.",
                                                           reader.getCurrentBSONType()));
        }
    }

    boolean canEncode(final Object object) {
        return object == null
               || bsonCodecs.canEncode(object.getClass())
               || object.getClass().isArray()
               || object instanceof Map
               || object instanceof Iterable;
    }

    public boolean canDecode(final Class<?> theClass) {
        return theClass.getClass().isArray()
               || bsonCodecs.canDecode(theClass)
               || iterableCodec.getEncoderClass().isInstance(theClass)
               || mapCodec.getEncoderClass().isAssignableFrom(theClass);
    }

    public static class Builder {
        private BSONCodecs bsonCodecs;

        public Builder primitiveCodecs(final BSONCodecs aBSONCodecs) {
            this.bsonCodecs = aBSONCodecs;
            return this;
        }

        public Codecs build() {
            return new Codecs(bsonCodecs, new QueryFieldNameValidator(), new EncoderRegistry());
        }
    }
}
