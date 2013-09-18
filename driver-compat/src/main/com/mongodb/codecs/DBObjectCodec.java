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

package com.mongodb.codecs;

import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBObjectFactory;
import com.mongodb.DBRef;
import com.mongodb.DBRefBase;
import org.bson.BSON;
import org.bson.BSONReader;
import org.bson.BSONType;
import org.bson.BSONWriter;
import org.bson.types.BasicBSONList;
import org.bson.types.Binary;
import org.bson.types.CodeWScope;
import org.bson.types.DBPointer;
import org.bson.types.Symbol;
import org.mongodb.Codec;
import org.mongodb.MongoException;
import org.mongodb.codecs.BSONCodecs;
import org.mongodb.codecs.validators.QueryFieldNameValidator;
import org.mongodb.codecs.validators.Validator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mongodb.MongoExceptions.mapException;

@SuppressWarnings("rawtypes")
public class DBObjectCodec implements Codec<DBObject> {

    private final BSONCodecs bsonCodecs;
    private final Validator<String> fieldNameValidator;
    private final DB db;
    private final DBObjectFactory objectFactory;


    public DBObjectCodec(final DB db, final BSONCodecs bsonCodecs,
                         final Validator<String> fieldNameValidator, final DBObjectFactory objectFactory) {
        if (bsonCodecs == null) {
            throw new IllegalArgumentException("primitiveCodecs is null");
        }
        this.bsonCodecs = bsonCodecs;
        this.db = db;
        this.fieldNameValidator = fieldNameValidator;
        this.objectFactory = objectFactory;
    }

    public DBObjectCodec() {
        this(null, BSONCodecs.createDefault(), new QueryFieldNameValidator(), new DBObjectFactory());
    }

    //TODO: what about BSON Exceptions?
    @Override
    public void encode(final BSONWriter bsonWriter, final DBObject document) {
        bsonWriter.writeStartDocument();

        beforeFields(bsonWriter, document);

        for (final String key : document.keySet()) {
            validateField(key);
            if (skipField(key)) {
                continue;
            }
            bsonWriter.writeName(key);
            writeValue(bsonWriter, document.get(key));
        }
        bsonWriter.writeEndDocument();
    }

    protected void beforeFields(final BSONWriter bsonWriter, final DBObject document) {
    }

    protected boolean skipField(final String key) {
        return false;
    }

    protected void validateField(final String key) {
        fieldNameValidator.validate(key);
    }

    @SuppressWarnings("unchecked")
    protected void writeValue(final BSONWriter bsonWriter, final Object initialValue) {
        final Object value = BSON.applyEncodingHooks(initialValue);
        try {
            if (value instanceof DBRefBase) {
                encodeDBRef(bsonWriter, (DBRefBase) value);
            } else if (value instanceof BasicBSONList) {
                encodeIterable(bsonWriter, (BasicBSONList) value);
            } else if (value instanceof DBObject) {
                encodeEmbeddedObject(bsonWriter, ((DBObject) value).toMap());
            } else if (value instanceof Map) {
                encodeEmbeddedObject(bsonWriter, (Map<String, Object>) value);
            } else if (value instanceof Iterable) {
                encodeIterable(bsonWriter, (Iterable) value);
            } else if (value instanceof CodeWScope) {
                encodeCodeWScope(bsonWriter, (CodeWScope) value);
            } else if (value instanceof byte[]) {
                bsonCodecs.encode(bsonWriter, new Binary((byte[]) value));
            } else if (value != null && value.getClass().isArray()) {
                encodeArray(bsonWriter, value);
            } else if (value instanceof Symbol) {
                bsonWriter.writeSymbol(((Symbol) value).getSymbol());
            } else {
                bsonCodecs.encode(bsonWriter, value);
            }
        } catch (final MongoException e) {
            throw mapException(e);
        }
    }

    private void encodeEmbeddedObject(final BSONWriter bsonWriter, final Map<String, Object> document) {
        bsonWriter.writeStartDocument();

        for (final Map.Entry<String, Object> entry : document.entrySet()) {
            validateField(entry.getKey());
            bsonWriter.writeName(entry.getKey());
            writeValue(bsonWriter, entry.getValue());
        }
        bsonWriter.writeEndDocument();
    }

    private void encodeArray(final BSONWriter bsonWriter, final Object value) {
        bsonWriter.writeStartArray();

        final int size = Array.getLength(value);
        for (int i = 0; i < size; i++) {
            writeValue(bsonWriter, Array.get(value, i));
        }

        bsonWriter.writeEndArray();
    }

    private void encodeDBRef(final BSONWriter bsonWriter, final DBRefBase dbRef) {
        bsonWriter.writeStartDocument();

        bsonWriter.writeString("$ref", dbRef.getRef());
        bsonWriter.writeName("$id");
        writeValue(bsonWriter, dbRef.getId());

        bsonWriter.writeEndDocument();
    }

    @SuppressWarnings("unchecked")
    private void encodeCodeWScope(final BSONWriter bsonWriter, final CodeWScope value) {
        bsonWriter.writeJavaScriptWithScope(value.getCode());
        encodeEmbeddedObject(bsonWriter, value.getScope().toMap());
    }

    private void encodeIterable(final BSONWriter bsonWriter, final Iterable iterable) {
        bsonWriter.writeStartArray();
        for (final Object cur : iterable) {
            writeValue(bsonWriter, cur);
        }
        bsonWriter.writeEndArray();
    }

    @Override
    public DBObject decode(final BSONReader reader) {
        final List<String> path = new ArrayList<String>(10);
        return readDocument(reader, path);
    }

    @Override
    public Class<DBObject> getEncoderClass() {
        return DBObject.class;
    }

    private Object readValue(final BSONReader reader, final String fieldName, final List<String> path) {
        final Object initialRetVal;
        try {
            final BSONType bsonType = reader.getCurrentBSONType();

            if (bsonType.isContainer() && fieldName != null) {
                //if we got into some new context like nested document or array
                path.add(fieldName);
            }

            switch (bsonType) {
                case DOCUMENT:
                    initialRetVal = verifyForDBRef(readDocument(reader, path));
                    break;
                case ARRAY:
                    initialRetVal = readArray(reader, path);
                    break;
                case JAVASCRIPT_WITH_SCOPE: //custom for driver-compat types
                    initialRetVal = readCodeWScope(reader, path);
                    break;
                case DB_POINTER: //custom for driver-compat types
                    final DBPointer dbPointer = reader.readDBPointer();
                    initialRetVal = new DBRef(db, dbPointer.getNamespace(), dbPointer.getId());
                    break;
                default:
                    initialRetVal = bsonCodecs.decode(reader);
            }

            if (bsonType.isContainer() && fieldName != null) {
                //step out of current context to a parent
                path.remove(fieldName);
            }
        } catch (MongoException e) {
            throw mapException(e);
        }

        return BSON.applyDecodingHooks(initialRetVal);
    }

    private List readArray(final BSONReader reader, final List<String> path) {
        reader.readStartArray();
        final BasicDBList list = new BasicDBList();
        while (reader.readBSONType() != BSONType.END_OF_DOCUMENT) {
            list.add(readValue(reader, null, path));   // TODO: why is this a warning?
        }
        reader.readEndArray();
        return list;
    }

    private DBObject readDocument(final BSONReader reader, final List<String> path) {
        final DBObject document = objectFactory.getInstance(path);

        reader.readStartDocument();
        while (reader.readBSONType() != BSONType.END_OF_DOCUMENT) {
            final String fieldName = reader.readName();
            document.put(fieldName, readValue(reader, fieldName, path));
        }

        reader.readEndDocument();
        return document;
    }

    private CodeWScope readCodeWScope(final BSONReader reader, final List<String> path) {
        return new CodeWScope(reader.readJavaScriptWithScope(), readDocument(reader, path));
    }

    private Object verifyForDBRef(final DBObject document) {
        if (document.containsField("$ref") && document.containsField("$id")) {
            return new DBRef(db, document);
        } else {
            return document;
        }
    }
}

