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

/**
 * Converts byte arrays of length 16 into a UUID and vice versa.  The byte array format is the first 8 bytes are the
 * most significant bits, encoded in a big endian fashion, and the second 8 bytes are the least significant bits, also
 * big endian.
 */
public class BigEndianUUIDTranslator implements ByteTranslator<UUID> {

    @Override
    public byte[] toBytes(final UUID uuid) {
        final byte[] bytes = new byte[16];

        writeLongToArrayBigEndian(bytes, 0, uuid.getMostSignificantBits());
        writeLongToArrayBigEndian(bytes, 8, uuid.getLeastSignificantBits());

        return bytes;
    }

    @Override
    public UUID fromBytes(final byte[] bytes) {
        return new UUID(readLongFromArrayBigEndian(bytes, 0), readLongFromArrayBigEndian(bytes, 8));

    }

    static void writeLongToArrayBigEndian(final byte[] bytes, final int offset, final long x) {
        bytes[offset] = (byte) (0xFFL & (x >> 56));
        bytes[offset + 1] = (byte) (0xFFL & (x >> 48));
        bytes[offset + 2] = (byte) (0xFFL & (x >> 40));
        bytes[offset + 3] = (byte) (0xFFL & (x >> 32));
        bytes[offset + 4] = (byte) (0xFFL & (x >> 24));
        bytes[offset + 5] = (byte) (0xFFL & (x >> 16));
        bytes[offset + 6] = (byte) (0xFFL & (x >> 8));
        bytes[offset + 7] = (byte) (0xFFL & (x));
    }

    static long readLongFromArrayBigEndian(final byte[] bytes, final int offset) {
        long x = 0;
        x |= (0xFFL & bytes[offset]) << 56;
        x |= (0xFFL & bytes[offset + 1]) << 48;
        x |= (0xFFL & bytes[offset + 2]) << 40;
        x |= (0xFFL & bytes[offset + 3]) << 32;
        x |= (0xFFL & bytes[offset + 4]) << 24;
        x |= (0xFFL & bytes[offset + 5]) << 16;
        x |= (0xFFL & bytes[offset + 6]) << 8;
        x |= (0xFFL & bytes[offset + 7]);
        return x;
    }
}
