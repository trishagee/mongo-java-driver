package org.bson.translators;

import java.util.UUID;

import static org.bson.translators.BigEndianLongTranslator.readLongFromArrayBigEndian;
import static org.bson.translators.BigEndianLongTranslator.writeLongToArrayBigEndian;

public class CSharpUUIDTranslator implements ByteTranslator{
    @Override
    public byte[] toBytes(UUID uuid) {
        byte[] bytes = new byte[16];

        writeLongToArrayCSharpLegacy( bytes, 0, uuid.getMostSignificantBits() );
        writeLongToArrayBigEndian(bytes, 8, uuid.getLeastSignificantBits());

        return bytes;
    }

    public UUID fromBytes(byte[] bytes) {
        return new UUID(readLongFromArrayCSharpLegacy( bytes, 0 ), readLongFromArrayBigEndian(bytes, 8));
    }

    private void writeLongToArrayCSharpLegacy(byte[] bytes, int offset, long x) {
        bytes[offset + 0] = (byte)(0xFFL & ( x >> 32 ) );
        bytes[offset + 1] = (byte)(0xFFL & ( x >> 40 ) );
        bytes[offset + 2] = (byte)(0xFFL & ( x >> 48 ) );
        bytes[offset + 3] = (byte)(0xFFL & ( x >> 56 ) );
        bytes[offset + 4] = (byte)(0xFFL & ( x >> 16 ) );
        bytes[offset + 5] = (byte)(0xFFL & ( x >> 24 ) );
        bytes[offset + 6] = (byte)(0xFFL & ( x >> 0 ) );
        bytes[offset + 7] = (byte)(0xFFL & ( x >> 8 ) );
    }

    private long readLongFromArrayCSharpLegacy(byte[] bytes, int offset) {
        long x = 0;
        x |= ( 0xFFL & bytes[offset+0] ) << 32;
        x |= ( 0xFFL & bytes[offset+1] ) << 40;
        x |= ( 0xFFL & bytes[offset+2] ) << 48;
        x |= ( 0xFFL & bytes[offset+3] ) << 56;
        x |= ( 0xFFL & bytes[offset+4] ) << 16;
        x |= ( 0xFFL & bytes[offset+5] ) << 24;
        x |= ( 0xFFL & bytes[offset+6] ) << 0;
        x |= ( 0xFFL & bytes[offset+7] ) << 8;
        return x;
    }
}
