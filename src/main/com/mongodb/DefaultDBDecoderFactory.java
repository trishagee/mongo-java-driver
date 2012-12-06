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

import org.bson.options.BSONOptions;

/**
 * Default implementation of the DBDecoderFactory that optionally takes BSONOptions to pass onto the Decoder.
 * This will create a DefaultDBDecoder which is the standard implementation of DBDecoder.
 *
 * @see DefaultDBDecoder
 */
public class DefaultDBDecoderFactory implements DBDecoderFactory {
    private final BSONOptions options;

    /**
     * Create a DBDecoderFactory with the default BSONOptions settings.
     *
     * @see BSONOptions
     */
    public DefaultDBDecoderFactory() {
        this(new BSONOptions.Builder().build());
    }

    /**
     * Takes a set of options to pass on to the created DBDecoderFactory
     *
     * @param options the set of options for the decoder to use
     */
    public DefaultDBDecoderFactory(final BSONOptions options) {
        this.options = options;
    }

    @Override
    public DBDecoder create() {
        return new DefaultDBDecoder(options);
    }
}
