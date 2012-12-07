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

import org.bson.BSONOptions;

/**
 * Default implementation of DBEncoderFactory that creates a DefaultDBEncoder for encoding messages to the
 * MongoDB server.  This factory optionally takes BSONOptions for overriding some of the default encoding
 * functionality.
 *
 * @see DefaultDBEncoder
 */
public class DefaultDBEncoderFactory implements DBEncoderFactory {
    private final BSONOptions options;

    /**
     * Uses the default BSONOptions when creating a DBEncoder
     *
     * @see BSONOptions
     */
    public DefaultDBEncoderFactory() {
        this(new BSONOptions.Builder().build());
    }

    /**
     * Use the provided BSONOptions when creating DBEncoder.
     *
     * @param options the specific BSONOptions for the encoder to use.
     */
    public DefaultDBEncoderFactory(final BSONOptions options) {
        this.options = options;
    }

    @Override
    public DBEncoder create() {
        return new DefaultDBEncoder(options);
    }
}
