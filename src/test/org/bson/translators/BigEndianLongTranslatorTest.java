package org.bson.translators;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

public class BigEndianLongTranslatorTest {
    @Test
    public void shouldEncodeLongAsBigEndian() throws IOException {
        // Given
        final BigEndianLongTranslator longTranslator = new BigEndianLongTranslator();

        // When
        final byte[] actualBytes = longTranslator.toBytes(new UUID(2L, 1L));

        // Then
        final byte[] expectedBytes = {0, 0, 0, 0, 0, 0, 0, 2,
                                      0, 0, 0, 0, 0, 0, 0, 1};  //8 bytes for long, most significant digits first
        assertEquals(actualBytes, expectedBytes);
    }

    @Test
    public void shouldReadBigEndianEncodedLongs() {
        // Given
        final BigEndianLongTranslator longTranslator = new BigEndianLongTranslator();

        // When
        final byte[] bytesToRead = {0, 0, 0, 0, 0, 0, 0, 2,
                                    0, 0, 0, 0, 0, 0, 0, 1};  //8 bytes for long, most significant digits first
        final UUID actualUUID = longTranslator.fromBytes(bytesToRead);

        // Then
        final UUID expectedUUID = new UUID(2L, 1L);
        assertEquals(actualUUID, expectedUUID);
    }
}
