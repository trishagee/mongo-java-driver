/*
 * Copyright (c) 2008 - 2012 10gen, Inc. <http://10gen.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.bson.translators;

/**
 * Interface for classes that provide functionality to convert from Objects of type T to bytes, and vice versa.  For use
 * by BSONEncoder and BSONDecoder implementations.
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
     *
     * @param bytes the serialised representation of the object
     * @return an Object of type T, deserialized from bytes
     */
    T fromBytes(byte[] bytes);
}
