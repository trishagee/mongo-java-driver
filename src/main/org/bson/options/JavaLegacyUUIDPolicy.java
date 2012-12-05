package org.bson.options;

import org.bson.UUIDRepresentation;

/**
 * This policy Singleton can be used to tell the BasicBSONEncoder and BasicBSONDecoder to use the old Java way of
 * encoding and decoding UUIDs into BSON bytes.
 */
public final class JavaLegacyUUIDPolicy implements BSONOptions {
    public static final JavaLegacyUUIDPolicy INSTANCE = new JavaLegacyUUIDPolicy();

    private JavaLegacyUUIDPolicy() {
    }

    @Override
    public UUIDRepresentation getUuidRepresentation() {
        return UUIDRepresentation.JAVA_LEGACY;
    }
}
