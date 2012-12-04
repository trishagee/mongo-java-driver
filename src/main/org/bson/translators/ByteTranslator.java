package org.bson.translators;

import java.util.UUID;

//TODO: generics?
public interface ByteTranslator {
    byte[] toBytes(final UUID val);

    UUID fromBytes(byte[] bytes);
}
