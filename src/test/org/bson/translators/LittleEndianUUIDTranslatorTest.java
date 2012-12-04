package org.bson.translators;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

public class LittleEndianUUIDTranslatorTest {

    @Test
    public void shouldEncodeLongAsLittleEndian() throws IOException {
        // Given
        final LittleEndianUUIDTranslator longTranslator = new LittleEndianUUIDTranslator();

        // When
        final byte[] actualBytes = longTranslator.toBytes(new UUID(2L, 1L));

        // Then
        final byte[] expectedBytes = {2, 0, 0, 0, 0, 0, 0, 0,
                                      1, 0, 0, 0, 0, 0, 0, 0};  //8 bytes for long, least significant digits first
        assertEquals(actualBytes, expectedBytes);
    }

    @Test
    public void shouldReadLittleEndianEncodedLongs() {
        // Given
        final LittleEndianUUIDTranslator longTranslator = new LittleEndianUUIDTranslator();

        // When
        final byte[] bytesToRead = {2, 0, 0, 0, 0, 0, 0, 0,
                                    1, 0, 0, 0, 0, 0, 0, 0};  //8 bytes for long, least significant digits first
        final UUID actualUUID = longTranslator.fromBytes(bytesToRead);

        // Then
        final UUID expectedUUID = new UUID(2L, 1L);
        assertEquals(actualUUID, expectedUUID);
    }
}
