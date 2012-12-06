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

package org.bson.options;

import org.bson.UUIDRepresentation;
import org.bson.util.annotations.Immutable;

/**
 * Class that contains all the options which can be configured for the BSON encoding and decoding.  This is designed to
 * be immutable, and should be built with the inner Builder class.
 */
@Immutable
public final class BSONOptions {
    private final UUIDRepresentation uuidRepresentation;

    private BSONOptions(final Builder builder) {
        uuidRepresentation = builder.uuidRepresentation;
    }

    /**
     * Returns the UUIDRepresentation enum, this can be used by BSON encoders and decoders to determine how to
     * serialise and deserialise the UUID.
     *
     * @return the UUIDRepresentation
     */
    public UUIDRepresentation getUUIDRepresentation() {
        return uuidRepresentation;
    }

    /**
     * Builder class for DefaultBSONOptions that provides all the default options, and allows users to override
     * the values they care about.
     */
    public static class Builder {
        private UUIDRepresentation uuidRepresentation = UUIDRepresentation.JAVA_LEGACY;

        public final Builder uuidRepresentation(final UUIDRepresentation uuidRepresentation) {
            this.uuidRepresentation = uuidRepresentation;
            return this;
        }

        public final BSONOptions build() {
            return new BSONOptions(this);
        }
    }
}
