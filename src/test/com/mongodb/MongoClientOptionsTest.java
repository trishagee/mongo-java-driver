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

package com.mongodb;

import org.testng.annotations.Test;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class MongoClientOptionsTest {

    @Test
    public void testBuilderDefaults() {
        final MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        final MongoClientOptions options = builder.build();
        assertEquals(DefaultDBDecoder.FACTORY, options.getDbDecoderFactory());
        assertEquals(DefaultDBEncoder.FACTORY, options.getDbEncoderFactory());
        assertEquals(null, options.getDescription());
        assertEquals(SocketFactory.getDefault(), options.getSocketFactory());
        assertEquals(WriteConcern.ACKNOWLEDGED, options.getWriteConcern());
        assertEquals(100, options.getConnectionsPerHost());
        assertEquals(10000, options.getConnectTimeout());
        assertEquals(0, options.getMaxAutoConnectRetryTime());
        assertEquals(ReadPreference.primary(), options.getReadPreference());
        assertEquals(5, options.getThreadsAllowedToBlockForConnectionMultiplier());
        assertEquals(false, options.isSocketKeepAlive());
        assertEquals(true, options.isCursorFinalizerEnabled());
        assertEquals(false, options.isAutoConnectRetry());
    }

    @Test
    public void testIllegalArguments() {
        final MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        try {
            builder.dbDecoderFactory(null);
            fail("Should throw an IllegalArgumentException if a null DBDecoderFactory is passed in");
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.dbEncoderFactory(null);
            fail("Should throw an IllegalArgumentException if a null DBEncoderFactory is passed in");
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.socketFactory(null);
            fail("Should throw an IllegalArgumentException if a null SocketFactory is passed in");
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.writeConcern(null);
            fail("Should throw an IllegalArgumentException if a null WriteConcern is passed in");
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.readPreference(null);
            fail("Should throw an IllegalArgumentException if a null ReadPreference is passed in");
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.connectionsPerHost(0);
            fail("Should throw an IllegalArgumentException if connectionsPerHost is not greater than zero");
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.connectTimeout(-1);
            fail("Should throw an IllegalArgumentException if connectionTimeout is less than zero");
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.maxAutoConnectRetryTime(-1);
            fail("Should throw an IllegalArgumentException if maxAutoConnectRetryTime is less than zero");
        } catch (IllegalArgumentException e) {
            // all good
        }
        try {
            builder.threadsAllowedToBlockForConnectionMultiplier(0);
            fail("Should throw an IllegalArgumentException if threadsAllowedToBlockForConnectionMultiplier " +
                    "is not greater than zero");
        } catch (IllegalArgumentException e) {
            // all good
        }

    }

    @Test
    public void testBuilderBuild() {
        final MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
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

        final SocketFactory socketFactory = SSLSocketFactory.getDefault();
        builder.socketFactory(socketFactory);

        final DBEncoderFactory encoderFactory = new DBEncoderFactory() {
            public DBEncoder create() {
                return null;
            }
        };
        builder.dbEncoderFactory(encoderFactory);

        final DBDecoderFactory decoderFactory = new DBDecoderFactory() {
            public DBDecoder create() {
                return null;
            }
        };
        builder.dbDecoderFactory(decoderFactory);

        final MongoClientOptions options = builder.build();

        assertEquals("test", options.getDescription());
        assertEquals(ReadPreference.secondary(), options.getReadPreference());
        assertEquals(WriteConcern.JOURNAL_SAFE, options.getWriteConcern());
        assertEquals(true, options.isAutoConnectRetry());
        assertEquals(500, options.getConnectionsPerHost());
        assertEquals(100, options.getConnectTimeout());
        assertEquals(300, options.getMaxAutoConnectRetryTime());
        assertEquals(1, options.getThreadsAllowedToBlockForConnectionMultiplier());
        assertEquals(true, options.isSocketKeepAlive());
        assertEquals(true, options.isCursorFinalizerEnabled());

        assertEquals(socketFactory, options.getSocketFactory());
        assertEquals(encoderFactory, options.getDbEncoderFactory());
        assertEquals(decoderFactory, options.getDbDecoderFactory());
    }
}
