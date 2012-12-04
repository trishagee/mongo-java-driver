package org.bson.options;

import org.bson.EncoderDecoderOptions;
import org.bson.UUIDRepresentation;

/**
 * This policy Singleton can be used to tell the BasicBSONEncoder and BasicBSONDecoder to use the new Standard way to
 * encode UUIDs to and from byte arrays.
 */
public final class StandardUUIDPolicy implements EncoderDecoderOptions {
    public static final StandardUUIDPolicy INSTANCE = new StandardUUIDPolicy();

    private StandardUUIDPolicy() {
    }

    @Override
    public UUIDRepresentation getUuidRepresentation() {
        return UUIDRepresentation.STANDARD;
    }
}
