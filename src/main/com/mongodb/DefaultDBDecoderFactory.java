package com.mongodb;

import org.bson.options.BSONOptions;
import org.bson.options.JavaLegacyUUIDPolicy;

/**
 * Default implementation of the DBDecoderFactory that optionally takes BSONOptions to pass onto the Decoder.
 * This will create a DefaultDBDecoder which is the standard implementation of DBDecoder.
 *
 * @see DefaultDBDecoder
 */
public class DefaultDBDecoderFactory implements DBDecoderFactory {
    private final BSONOptions options;

    /**
     * Create a DBDecoderFactory with the default BSONOptions settings.
     */
    public DefaultDBDecoderFactory() {
        this(JavaLegacyUUIDPolicy.INSTANCE);
    }

    /**
     * Takes a set of options to pass on to the created DBDecoderFactory
     * @param options the set of options for the decoder to use
     */
    public DefaultDBDecoderFactory(final BSONOptions options) {
        this.options = options;
    }

    @Override
    public DBDecoder create() {
        return new DefaultDBDecoder(options);
    }
}
