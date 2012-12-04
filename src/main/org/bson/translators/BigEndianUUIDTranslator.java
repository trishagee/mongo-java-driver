package org.bson.translators;

import java.util.UUID;

/**
 * Converts byte arrays of length 16 into a UUID and vice versa.  The byte array format is the first 8 bytes are the
 * most significant bits, encoded in a big endian fashion, and the second 8 bytes are the least significant bits,
 * also big endian.
 */
public class BigEndianUUIDTranslator implements ByteTranslator<UUID> {

    @Override
    public byte[] toBytes(UUID uuid) {
        byte[] bytes = new byte[16];

        writeLongToArrayBigEndian(bytes, 0, uuid.getMostSignificantBits());
        writeLongToArrayBigEndian(bytes, 8, uuid.getLeastSignificantBits());

        return bytes;
    }

    @Override
    public UUID fromBytes(byte[] bytes) {
        return new UUID(readLongFromArrayBigEndian(bytes, 0), readLongFromArrayBigEndian(bytes, 8));

    }

    static void writeLongToArrayBigEndian(byte[] bytes, int offset, long x) {
        bytes[offset] = (byte) (0xFFL & (x >> 56));
        bytes[offset + 1] = (byte) (0xFFL & (x >> 48));
        bytes[offset + 2] = (byte) (0xFFL & (x >> 40));
        bytes[offset + 3] = (byte) (0xFFL & (x >> 32));
        bytes[offset + 4] = (byte) (0xFFL & (x >> 24));
        bytes[offset + 5] = (byte) (0xFFL & (x >> 16));
        bytes[offset + 6] = (byte) (0xFFL & (x >> 8));
        bytes[offset + 7] = (byte) (0xFFL & (x));
    }

    static long readLongFromArrayBigEndian(byte[] bytes, int offset) {
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
