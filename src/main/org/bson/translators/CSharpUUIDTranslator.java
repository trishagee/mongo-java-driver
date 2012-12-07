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

package org.bson.translators;

import java.util.UUID;

import static org.bson.translators.BigEndianUUIDTranslator.readLongFromArrayBigEndian;
import static org.bson.translators.BigEndianUUIDTranslator.writeLongToArrayBigEndian;

/**
 * Translator that understand UUIDs encoded using the C# driver.  This will serialize/deserialize Java UUIDs using the
 * C# legacy standard.  Using this Translator assumes all existing IDs were encoded using the C# legacy standard, and
 * will continue serialize IDs this way.
 */
public class CSharpUUIDTranslator implements ByteTranslator<UUID> {

    @Override
    public byte[] toBytes(final UUID uuid) {
        final byte[] bytes = new byte[16];

        writeLongToArrayCSharpLegacy(bytes, 0, uuid.getMostSignificantBits());
        writeLongToArrayBigEndian(bytes, 8, uuid.getLeastSignificantBits());

        return bytes;
    }

    @Override
    public UUID fromBytes(final byte[] bytes) {
        return new UUID(readLongFromArrayCSharpLegacy(bytes, 0), readLongFromArrayBigEndian(bytes, 8));
    }

    private void writeLongToArrayCSharpLegacy(final byte[] bytes, final int offset, final long x) {
        bytes[offset] = (byte) (0xFFL & (x >> 32));
        bytes[offset + 1] = (byte) (0xFFL & (x >> 40));
        bytes[offset + 2] = (byte) (0xFFL & (x >> 48));
        bytes[offset + 3] = (byte) (0xFFL & (x >> 56));
        bytes[offset + 4] = (byte) (0xFFL & (x >> 16));
        bytes[offset + 5] = (byte) (0xFFL & (x >> 24));
        bytes[offset + 6] = (byte) (0xFFL & (x));
        bytes[offset + 7] = (byte) (0xFFL & (x >> 8));
    }

    private long readLongFromArrayCSharpLegacy(final byte[] bytes, final int offset) {
        long x = 0;
        x |= (0xFFL & bytes[offset]) << 32;
        x |= (0xFFL & bytes[offset + 1]) << 40;
        x |= (0xFFL & bytes[offset + 2]) << 48;
        x |= (0xFFL & bytes[offset + 3]) << 56;
        x |= (0xFFL & bytes[offset + 4]) << 16;
        x |= (0xFFL & bytes[offset + 5]) << 24;
        x |= (0xFFL & bytes[offset + 6]);
        x |= (0xFFL & bytes[offset + 7]) << 8;
        return x;
    }
}
