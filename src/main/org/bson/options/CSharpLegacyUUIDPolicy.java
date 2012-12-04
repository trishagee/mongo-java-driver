package org.bson.options;

import org.bson.EncoderDecoderOptions;
import org.bson.UUIDRepresentation;

/**
 * This policy Singleton can be used to tell the BasicBSONEncoder and BasicBSONDecoder to use the old C# way of
 * encoding and decoding UUIDs into BSON bytes.
 */
public final class CSharpLegacyUUIDPolicy implements EncoderDecoderOptions {
    public static final CSharpLegacyUUIDPolicy INSTANCE = new CSharpLegacyUUIDPolicy();

    private CSharpLegacyUUIDPolicy() {
    }

    @Override
    public UUIDRepresentation getUuidRepresentation() {
        throw new IllegalStateException("Not implemented yet!");
    }
}
