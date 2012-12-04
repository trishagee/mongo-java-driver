package org.bson.translators;

/**
 * Interface for classes that provide functionality to convert from Objects of type T to bytes, and vice versa.  For
 * use by BSONEncoder and BSONDecoder implementations.
 *
 * @param <T> the type to translate from and to bytes
 */
public interface ByteTranslator<T> {

    /**
     * Generates an array of bytes from an object of the given type T
     *
     * @param objectToTranslate the Object to serialize into a byte array
     * @return the byte array representation of objectToTranslate
     */
    byte[] toBytes(final T objectToTranslate);

    /**
     * Creates an object of type T from the given array of bytes
     * @param bytes the serialised representation of the object
     * @return an Object of type T, deserialized from bytes
     */
    T fromBytes(byte[] bytes);
}
