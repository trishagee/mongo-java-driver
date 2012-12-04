package org.bson.translators;

import org.bson.io.Bits;

import java.util.UUID;

/**
 * Converts byte arrays of length 16 into a UUID and vice versa.  The byte array format is the first 8 bytes are the
 * most significant bits, encoded in a little endian fashion, and the second 8 bytes are the least significant bits,
 * also little endian.
 */
public class LittleEndianUUIDTranslator implements ByteTranslator<UUID> {

    @Override
    public byte[] toBytes(UUID uuid) {
        byte[] bytes = new byte[16];

        writeLongToArrayLittleEndian(bytes, 0, uuid.getMostSignificantBits());
        writeLongToArrayLittleEndian(bytes, 8, uuid.getLeastSignificantBits());

        return bytes;
    }

    public UUID fromBytes(byte[] bytes) {
        return new UUID(readLongFromArrayLittleEndian(bytes, 0), readLongFromArrayLittleEndian(bytes, 8));
    }

    private static void writeLongToArrayLittleEndian(byte[] bytes, int offset, long x) {
        bytes[offset] = (byte) (0xFFL & (x));
        bytes[offset + 1] = (byte) (0xFFL & (x >> 8));
        bytes[offset + 2] = (byte) (0xFFL & (x >> 16));
        bytes[offset + 3] = (byte) (0xFFL & (x >> 24));
        bytes[offset + 4] = (byte) (0xFFL & (x >> 32));
        bytes[offset + 5] = (byte) (0xFFL & (x >> 40));
        bytes[offset + 6] = (byte) (0xFFL & (x >> 48));
        bytes[offset + 7] = (byte) (0xFFL & (x >> 56));
    }

    private static long readLongFromArrayLittleEndian(byte[] bytes, int offset) {
        return Bits.readLong(bytes, offset);
    }
}
