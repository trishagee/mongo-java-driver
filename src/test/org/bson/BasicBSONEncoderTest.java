package org.bson;

import org.bson.io.BasicOutputBuffer;
import org.bson.options.BSONOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class BasicBSONEncoderTest {
    @Test
    public void shouldUseTheJavaLegacyRepresentationToEncodeUUIDsByDefault() throws IOException {
        // Given
        final BasicBSONEncoder bsonEncoder = new BasicBSONEncoder();

        final BasicOutputBuffer actualBuffer = new BasicOutputBuffer();
        bsonEncoder.set(actualBuffer);

        // When
        bsonEncoder.putUUID("_id", new UUID(2, 1));

        // Then
        final TestOutputStream outputStream = new TestOutputStream();
        actualBuffer.pipe(outputStream);

        // expected byte array = [ type, '_', 'i', 'd', 16, {long}, {long} ]
        // type =   5
        // '_'  =   95
        // 'i'  =  105
        // 'd'  =  100
        Assert.assertEquals(outputStream.getResults(), Arrays.asList(
                5,                // type (BINARY)
                95, 105, 100, 0,  // "_id"
                16, 0, 0, 0,      // int "16" (length)
                3,                // type (B_UUID_LEGACY)
                2, 0, 0, 0, 0, 0, 0, 0,
                1, 0, 0, 0, 0, 0, 0, 0)); //8 bytes for long, 2 longs for UUID, Little Endian for Java Legacy
    }

    @Test
    public void shouldUseTheProvidedUUIDRepresentationToEncodeUUIDs() throws IOException {
        // Given
        final BasicBSONEncoder bsonEncoder = new BasicBSONEncoder(new StandardEncoderOptions());

        final BasicOutputBuffer actualBuffer = new BasicOutputBuffer();
        bsonEncoder.set(actualBuffer);

        // When
        bsonEncoder.putUUID("_id", new UUID(2, 1));

        // Then
        final TestOutputStream outputStream = new TestOutputStream();
        actualBuffer.pipe(outputStream);

        // expected byte array = [ type, '_', 'i', 'd', 16, {long}, {long} ]
        // type =   5
        // '_'  =   95
        // 'i'  =  105
        // 'd'  =  100
        Assert.assertEquals(outputStream.getResults(), Arrays.asList(
                5,                // type (BINARY)
                95, 105, 100, 0,  // "_id"
                16, 0, 0, 0,      // int "16" (length)
                4,                // type (B_UUID_STANDARD)
                0, 0, 0, 0, 0, 0, 0, 2,
                0, 0, 0, 0, 0, 0, 0, 1)); //8 bytes for long, 2 longs for UUID, Big Endian for Standard encoding

    }

    private class StandardEncoderOptions implements BSONOptions {
        public UUIDRepresentation getUuidRepresentation() {
            return UUIDRepresentation.STANDARD;
        }
    }

}
