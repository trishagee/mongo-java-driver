package org.bson.translators;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

public class CSharpUUIDTranslatorTest {
    @Test
    public void shouldEncodeLong() throws IOException {
        // Given
        final CSharpUUIDTranslator longTranslator = new CSharpUUIDTranslator();

        // When
        final byte[] actualBytes = longTranslator.toBytes(new UUID(2L, 1L));

        // Then
        final byte[] expectedBytes = {0, 0, 0, 0, 0, 0, 2, 0,
                                      0, 0, 0, 0, 0, 0, 0, 1}; // Not really sure what's going on here....
        assertEquals(actualBytes, expectedBytes);
    }

    @Test
    public void shouldReadEncodedLongs() {
        // Given
        final CSharpUUIDTranslator longTranslator = new CSharpUUIDTranslator();

        // When
        final byte[] bytesToRead = {0, 0, 0, 0, 0, 0, 2, 0,
                                    0, 0, 0, 0, 0, 0, 0, 1}; // Not really sure what's going on here....
        final UUID actualUUID = longTranslator.fromBytes(bytesToRead);

        // Then
        final UUID expectedUUID = new UUID(2L, 1L);
        assertEquals(actualUUID, expectedUUID);
    }
}
