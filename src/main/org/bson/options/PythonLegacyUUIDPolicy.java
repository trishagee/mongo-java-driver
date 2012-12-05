package org.bson.options;

import org.bson.UUIDRepresentation;

/**
 * This policy Singleton can be used to tell the BasicBSONEncoder and BasicBSONDecoder to use the old Python way of
 * encoding and decoding UUIDs into BSON bytes.
 */
public final class PythonLegacyUUIDPolicy implements BSONOptions {
    public static final PythonLegacyUUIDPolicy INSTANCE = new PythonLegacyUUIDPolicy();

    private PythonLegacyUUIDPolicy() {
    }

    @Override
    public UUIDRepresentation getUuidRepresentation() {
        return UUIDRepresentation.PYTHON_LEGACY;
    }
}
