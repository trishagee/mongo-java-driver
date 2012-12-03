/*
 * Copyright (C) 2011 10gen Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// UUIDRepresentation.java

package org.bson;

import org.bson.translators.BigEndianLongTranslator;
import org.bson.translators.ByteTranslator;
import org.bson.translators.CSharpUUIDTranslator;
import org.bson.translators.LittleEndianLongTranslator;

import static org.bson.BSON.B_UUID_LEGACY;
import static org.bson.BSON.B_UUID_STANDARD;

public enum UUIDRepresentation {
    //TODO: not going to work because the encoders and decoders aren't mirror images
    //TODO: write test
    /**
     * Use the new standard representation for Guids (binary subtype 4 with bytes in network byte order).
     */
    STANDARD(B_UUID_STANDARD, new BigEndianLongTranslator()),
    /**
     * Use the representation used by older versions of the Java driver.
     */
    JAVA_LEGACY(B_UUID_LEGACY, new LittleEndianLongTranslator()),
    /**
     * Use the representation used by older versions of the Python driver.
     */
    PYTHON_LEGACY(B_UUID_LEGACY, new BigEndianLongTranslator()),
    /**
     * Use the representation used by older versions of the C# driver (including most community provided C# drivers).
     */
    C_SHARP_LEGACY(B_UUID_LEGACY, new CSharpUUIDTranslator());

    private byte binaryType;
    private ByteTranslator translator;

    private UUIDRepresentation(final byte binaryType, final ByteTranslator translator) {
        this.binaryType = binaryType;
        this.translator = translator;
    }

    public ByteTranslator getTranslator() {
        return translator;
    }

    public byte getBinaryType() {
        return binaryType;
    }
}
