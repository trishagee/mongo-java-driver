package com.mongodb;

import org.bson.options.BSONOptions;
import org.bson.options.JavaLegacyUUIDPolicy;

/**
 * Default implementation of DBEncoderFactory that creates a DefaultDBEncoder for encoding messages to the
 * MongoDB server.  This factory optionally takes BSONOptions for overriding some of the default encoding
 * functionality.
 *
 * @see DefaultDBEncoder
 */
public class DefaultDBEncoderFactory implements DBEncoderFactory {
    private final BSONOptions options;

    /**
     * Uses the default BSONOptions when creating a DBEncoder
     */
    public DefaultDBEncoderFactory() {
        this(JavaLegacyUUIDPolicy.INSTANCE);
    }

    /**
     * Use the provided BSONOptions when creating DBEncoder.
     * @param options the specific BSONOptions for the encoder to use.
     */
    public DefaultDBEncoderFactory(final BSONOptions options) {
        this.options = options;
    }

    @Override
    public DBEncoder create() {
        return new DefaultDBEncoder(options);
    }
}
