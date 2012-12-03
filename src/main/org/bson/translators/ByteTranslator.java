package org.bson.translators;

import java.util.UUID;

public interface ByteTranslator {
    byte[] toBytes(final UUID val);
}
