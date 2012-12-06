package org.bson.options;

import org.bson.UUIDRepresentation;

/**
 * Options needed to control behavior of DBEncoder and DBDecoder implementations.  These are separate from
 * MongoClientOptions because this is at the BSON layer, which is lower-level than MongoDB.
 */
public interface BSONOptions {

    /**
     * Get the particular UUIDRepresentation that is required for your implementation.
     *
     * @return the UUIDRepresentation to be used when encoding and decoding BSON
     */
    UUIDRepresentation getUUIDRepresentation();
}
