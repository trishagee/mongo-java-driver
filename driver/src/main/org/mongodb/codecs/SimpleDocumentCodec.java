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
import org.mongodb.Document;
import org.mongodb.codecs.validators.QueryFieldNameValidator;
import org.mongodb.codecs.validators.Validator;

import java.util.Map;

public class SimpleDocumentCodec implements Codec<Document> {
    private final Validator<String> fieldNameValidator;
    private final BSONCodecs bsonCodecs;

    public SimpleDocumentCodec(final BSONCodecs bsonCodecs) {
        this(bsonCodecs, new QueryFieldNameValidator());
    }

    protected SimpleDocumentCodec(final BSONCodecs bsonCodecs, final Validator<String> fieldNameValidator) {
        this.bsonCodecs = bsonCodecs;
        this.fieldNameValidator = fieldNameValidator;
    }

    @Override
    public void encode(final BSONWriter bsonWriter, final Document document) {
        bsonWriter.writeStartDocument();

        for (final Map.Entry<String, Object> entry : document.entrySet()) {
            fieldNameValidator.validate(entry.getKey());

            bsonWriter.writeName(entry.getKey());
            writeValue(bsonWriter, entry.getValue());
        }
        bsonWriter.writeEndDocument();
    }

    @SuppressWarnings("unchecked")
    protected void writeValue(final BSONWriter bsonWriter, final Object value) {
        bsonCodecs.encode(bsonWriter, value);
    }

    @Override
    public Document decode(final BSONReader reader) {
        final Document document = new Document();

        reader.readStartDocument();
        while (reader.readBSONType() != BSONType.END_OF_DOCUMENT) {
            final String fieldName = reader.readName();
            final Object value = bsonCodecs.decode(reader);
            document.put(fieldName, value);
        }

        reader.readEndDocument();
        return document;
    }

    @Override
    public Class<Document> getEncoderClass() {
        return Document.class;
    }

}
