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

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBObjectFactory;
import org.bson.BSONWriter;
import org.mongodb.CollectibleCodec;
import org.mongodb.IdGenerator;
import org.mongodb.codecs.BSONCodecs;
import org.mongodb.codecs.validators.FieldNameValidator;

/**
 * Codec for documents that go in collections, and therefore have an _id.  Ensures that the _id field is written
 * first.
 */
public class CollectibleDBObjectCodec extends DBObjectCodec implements CollectibleCodec<DBObject> {
    private static final String ID_FIELD_NAME = "_id";
    private final IdGenerator idGenerator;

    public CollectibleDBObjectCodec(final DB database, final BSONCodecs bsonCodecs,
                                    final IdGenerator idGenerator,
                                    final DBObjectFactory objectFactory) {
        super(database, bsonCodecs, new FieldNameValidator(), objectFactory);
        this.idGenerator = idGenerator;
    }

    @Override
    protected void beforeFields(final BSONWriter bsonWriter, final DBObject document) {
        if (document.get(ID_FIELD_NAME) == null) {
            document.put(ID_FIELD_NAME, idGenerator.generate());
        }
        bsonWriter.writeName(ID_FIELD_NAME);
        writeValue(bsonWriter, document.get(ID_FIELD_NAME));
    }

    @Override
    protected boolean skipField(final String key) {
        return key.equals(ID_FIELD_NAME);
    }

    @Override
    public Object getId(final DBObject document) {
        return document.get(ID_FIELD_NAME);
    }
}
