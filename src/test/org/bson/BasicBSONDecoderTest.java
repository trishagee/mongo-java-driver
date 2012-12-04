package org.bson;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.UUID;

public class BasicBSONDecoderTest {

    @Test
    public void shouldUseTheJavaLegacyRepresentationToDecodeUUIDsByDefault() throws IOException {
        // Given
        final BasicBSONDecoder bsonDecoder = new BasicBSONDecoder();

        final UUID expectedUUID = new UUID(2, 1);

        final byte[] binaryTypeWithUUIDAsBytes = {
                31, 0, 0, 0,            // message length
                5,                      // type (BINARY)
                95, 105, 100, 0,        // "_id"
                16, 0, 0, 0,            // int "16" (length)
                4,                      // type (B_UUID_STANDARD)
                2, 0, 0, 0, 0, 0, 0, 0, //
                1, 0, 0, 0, 0, 0, 0, 0, // 8 bytes for long, 2 longs for UUID, Little Endian for Default (Java) encoding
                0};                     // EOM

        // When
        final BasicBSONCallback callback = new BasicBSONCallback();
        bsonDecoder.decode(binaryTypeWithUUIDAsBytes, callback);

        // Then
        Assert.assertEquals(((BSONObject) callback.get()).get("_id"), expectedUUID);
    }

    @Test
    public void shouldUseTheProvidedUUIDRepresentationToDecodeUUIDs() throws IOException {
        // Given
        final BasicBSONDecoder bsonDecoder = new BasicBSONDecoder(new StandardDecoderOptions());

        final UUID expectedUUID = new UUID(2, 1);

        final byte[] binaryTypeWithUUIDAsBytes = {
                31, 0, 0, 0,            // message length
                5,                      // type (BINARY)
                95, 105, 100, 0,        // "_id"
                16, 0, 0, 0,            // int "16" (length)
                4,                      // type (B_UUID_STANDARD)
                0, 0, 0, 0, 0, 0, 0, 2,
                0, 0, 0, 0, 0, 0, 0, 1, // 8 bytes for long, 2 longs for UUID, Big Endian for Standard encoding
                0};                     // EOM

        // When
        final BasicBSONCallback callback = new BasicBSONCallback();
        bsonDecoder.decode(binaryTypeWithUUIDAsBytes, callback);

        // Then
        Assert.assertEquals(((BSONObject) callback.get()).get("_id"), expectedUUID);
    }

    private class StandardDecoderOptions implements EncoderDecoderOptions {
        public UUIDRepresentation getUuidRepresentation() {
            return UUIDRepresentation.STANDARD;
        }
    }
}
