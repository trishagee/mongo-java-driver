package com.mongodb.codecs;

import com.mongodb.DBRef;
import org.bson.BSONWriter;
import org.mongodb.Encoder;
import org.mongodb.codecs.BSONCodecs;

public class DBRefEncoder implements Encoder<DBRef> {
    private final BSONCodecs bsonCodecs;

    public DBRefEncoder(final BSONCodecs bsonCodecs) {
        this.bsonCodecs = bsonCodecs;
    }

    @Override
    public void encode(final BSONWriter bsonWriter, final DBRef value) {
        bsonWriter.writeStartDocument();

        bsonWriter.writeString("$ref", value.getRef());
        bsonWriter.writeName("$id");
        bsonCodecs.encode(bsonWriter, value.getId());

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<DBRef> getEncoderClass() {
        return DBRef.class;
    }
}
