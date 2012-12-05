package com.mongodb;

import junit.framework.Assert;
import org.bson.UUIDRepresentation;
import org.bson.io.BasicOutputBuffer;
import org.bson.options.BSONOptions;
import org.testng.annotations.Test;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import java.util.UUID;

import static junit.framework.Assert.assertTrue;

/**
 * Copyright (c) 2008 - 2012 10gen, Inc. <http://10gen.com>
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class MongoClientOptionsTest {

    @Test
    public void testBuilderDefaults() {
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        MongoClientOptions options = builder.build();
        Assert.assertEquals(DefaultDBDecoder.FACTORY, options.getDbDecoderFactory());
        Assert.assertEquals(DefaultDBEncoder.FACTORY, options.getDbEncoderFactory());
        Assert.assertEquals(null, options.getDescription());
        Assert.assertEquals(SocketFactory.getDefault(), options.getSocketFactory());
        Assert.assertEquals(WriteConcern.ACKNOWLEDGED, options.getWriteConcern());
        Assert.assertEquals(100, options.getConnectionsPerHost());
        Assert.assertEquals(10000, options.getConnectTimeout());
        Assert.assertEquals(0, options.getMaxAutoConnectRetryTime());
        Assert.assertEquals(ReadPreference.primary(), options.getReadPreference());
        Assert.assertEquals(5, options.getThreadsAllowedToBlockForConnectionMultiplier());
        Assert.assertEquals(false, options.isSocketKeepAlive());
        Assert.assertEquals(true, options.isCursorFinalizerEnabled());
        Assert.assertEquals(false, options.isAutoConnectRetry());
    }

    @Test
    public void testIllegalArguments() {
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        try {
          builder.dbDecoderFactory(null);
          Assert.fail();
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.dbEncoderFactory(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.socketFactory(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.writeConcern(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.readPreference(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.connectionsPerHost(0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.connectTimeout(-1);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.maxAutoConnectRetryTime(-1);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.threadsAllowedToBlockForConnectionMultiplier(0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // all good
        }

    }

    @Test
    public void testBuilderBuild() {
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.description("test");
        builder.readPreference(ReadPreference.secondary());
        builder.writeConcern(WriteConcern.JOURNAL_SAFE);
        builder.autoConnectRetry(true);
        builder.connectionsPerHost(500);
        builder.connectTimeout(100);
        builder.maxAutoConnectRetryTime(300);
        builder.threadsAllowedToBlockForConnectionMultiplier(1);
        builder.socketKeepAlive(true);
        builder.cursorFinalizerEnabled(true);

        SocketFactory socketFactory = SSLSocketFactory.getDefault();
        builder.socketFactory(socketFactory);

        DBEncoderFactory encoderFactory = new DBEncoderFactory() {
            public DBEncoder create() {
                return null;
            }
        };
        builder.dbEncoderFactory(encoderFactory);

        DBDecoderFactory decoderFactory = new DBDecoderFactory() {
            public DBDecoder create() {
                return null;
            }
        };
        builder.dbDecoderFactory(decoderFactory);

        MongoClientOptions options = builder.build();

        Assert.assertEquals("test", options.getDescription());
        Assert.assertEquals(ReadPreference.secondary(), options.getReadPreference());
        Assert.assertEquals(WriteConcern.JOURNAL_SAFE, options.getWriteConcern());
        Assert.assertEquals(true, options.isAutoConnectRetry());
        Assert.assertEquals(500, options.getConnectionsPerHost());
        Assert.assertEquals(100, options.getConnectTimeout());
        Assert.assertEquals(300, options.getMaxAutoConnectRetryTime());
        Assert.assertEquals(1, options.getThreadsAllowedToBlockForConnectionMultiplier());
        Assert.assertEquals(true, options.isSocketKeepAlive());
        Assert.assertEquals(true, options.isCursorFinalizerEnabled());

        Assert.assertEquals(socketFactory, options.getSocketFactory());
        Assert.assertEquals(encoderFactory, options.getDbEncoderFactory());
        Assert.assertEquals(decoderFactory, options.getDbDecoderFactory());
    }

    @Test
    public void shouldUseSuppliedBSONOptionsForEncoding() {
        final MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        final StubBSONOptions bsonOptions = new StubBSONOptions();
        builder.bsonOptions(bsonOptions);

        final MongoClientOptions mongoClientOptions = builder.build();
        final DBEncoder dbEncoder = mongoClientOptions.getDbEncoderFactory().create();
        dbEncoder.writeObject(new BasicOutputBuffer(), new BasicDBObject("_id", new UUID(3, 4)));

        //poor man's mocking - if this flag is set, then the BSONOptions I passed in are being used when we encode
        assertTrue(bsonOptions.getUuidRepresentationCalled);
    }

    @Test
    public void shouldUseSuppliedBSONOptionsForDecoding() {
        final MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        final StubBSONOptions bsonOptions = new StubBSONOptions();
        builder.bsonOptions(bsonOptions);

        final MongoClientOptions mongoClientOptions = builder.build();
        final DBDecoder dbEncoder = mongoClientOptions.getDbDecoderFactory().create();
        final byte[] binaryTypeWithUUIDAsBytes = {
                31, 0, 0, 0,            // message length
                5,                      // type (BINARY)
                95, 105, 100, 0,        // "_id"
                16, 0, 0, 0,            // int "16" (length)
                4,                      // type (B_UUID_STANDARD)
                2, 0, 0, 0, 0, 0, 0, 0, //
                1, 0, 0, 0, 0, 0, 0, 0, // 8 bytes for long, 2 longs for UUID, Little Endian for Default (Java) encoding
                0};                     // EOM
        dbEncoder.decode(binaryTypeWithUUIDAsBytes, new DefaultDBCallback(null));

        // poor man's mocking - if this flag is set, then the BSONOptions I passed in are being used when we decode
        assertTrue(bsonOptions.getUuidRepresentationCalled);
    }

    private class StubBSONOptions implements BSONOptions {
        private boolean getUuidRepresentationCalled = false;

        @Override
        public UUIDRepresentation getUuidRepresentation() {
            getUuidRepresentationCalled = true;
            return UUIDRepresentation.STANDARD;
        }
    }
}
