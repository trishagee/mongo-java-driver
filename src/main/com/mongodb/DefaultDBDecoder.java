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

import org.bson.BasicBSONDecoder;
import org.bson.BSONOptions;

import java.io.IOException;
import java.io.InputStream;

/**
 * The default implementation of DBDecoder for the driver.  Effectively a decorator for BasicBSONDecoder, one that
 * defines a DefaultDBCallback and provides the methods defined on DBDecoder, therefore designed to provide MongoDB
 * specific functionality for the BSON Decoder.
 */
public class DefaultDBDecoder extends BasicBSONDecoder implements DBDecoder {
    public static DBDecoderFactory FACTORY = new DefaultDBDecoderFactory();

    public DefaultDBDecoder() {
        super();
    }

    public DefaultDBDecoder(final BSONOptions options) {
        super(options);
    }

    public DBCallback getDBCallback(final DBCollection collection) {
        // brand new callback every time
        return new DefaultDBCallback(collection);
    }

    public DBObject decode(final byte[] b, final DBCollection collection) {
        final DBCallback cbk = getDBCallback(collection);
        cbk.reset();
        decode(b, cbk);
        return (DBObject) cbk.get();
    }

    public DBObject decode(final InputStream in, final DBCollection collection) throws IOException {
        final DBCallback cbk = getDBCallback(collection);
        cbk.reset();
        decode(in, cbk);
        return (DBObject) cbk.get();
    }
}
