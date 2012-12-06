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

/**
 * Default implementation of BSONOptions.  This is designed to be immutable, and should be built with the inner
 * Builder class.
 */
public final class DefaultBSONOptions implements BSONOptions {
    private final UUIDRepresentation uuidRepresentation;

    private DefaultBSONOptions(final Builder builder) {
        uuidRepresentation = builder.uuidRepresentation;
    }

    @Override
    public UUIDRepresentation getUUIDRepresentation() {
        return uuidRepresentation;
    }

    /**
     * Builder class for DefaultBSONOptions that provides all the default options, and allows users to override
     * the values they care about.
     */
    public static class Builder {
        private UUIDRepresentation uuidRepresentation = UUIDRepresentation.JAVA_LEGACY;

        public final void uuidRepresentation(final UUIDRepresentation uuidRepresentation) {
            this.uuidRepresentation = uuidRepresentation;
        }

        public final DefaultBSONOptions build() {
            return new DefaultBSONOptions(this);
        }
    }
}
