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

package org.bson;

import org.bson.translators.BigEndianUUIDTranslator;
import org.bson.translators.ByteTranslator;
import org.bson.translators.CSharpUUIDTranslator;
import org.bson.translators.LittleEndianUUIDTranslator;

import java.util.UUID;

import static org.bson.BSON.B_UUID_LEGACY;
import static org.bson.BSON.B_UUID_STANDARD;

/**
 * UUIDs can be encoded in a number of different ways.  This enum outlines the four standard ones, and provides a
 * ByteTranslator that can be used by the BSONEncoder and BSONDecoder to serialise or deserialise UUIDs on the wire.
 */
public enum UUIDRepresentation {
    /**
     * The new standard representation for Guids (binary subtype 4 with bytes in network byte order).
     */
    STANDARD(B_UUID_STANDARD, new BigEndianUUIDTranslator()),
    /**
     * The representation used by older versions of the Java driver.
     */
    JAVA_LEGACY(B_UUID_LEGACY, new LittleEndianUUIDTranslator()),
    /**
     * The representation used by older versions of the Python driver.
     */
    PYTHON_LEGACY(B_UUID_LEGACY, new BigEndianUUIDTranslator()),
    /**
     * The representation used by older versions of the C# driver (including most community provided C# drivers).
     */
    C_SHARP_LEGACY(B_UUID_LEGACY, new CSharpUUIDTranslator());

    private final byte binaryType;
    private final ByteTranslator<UUID> translator;

    private UUIDRepresentation(final byte binaryType, final ByteTranslator<UUID> translator) {
        this.binaryType = binaryType;
        this.translator = translator;
    }

    /**
     * @return a ByteTranslator that knows how to convert UUIDs to byte arrays and vice versa.
     */
    public ByteTranslator<UUID> getTranslator() {
        return translator;
    }

    /**
     * Currently returns either B_UUID_LEGACY or B_UUID_STANDARD.
     *
     * @return a byte representing the type of this UUID.
     */
    public byte getBinaryType() {
        return binaryType;
    }
}
