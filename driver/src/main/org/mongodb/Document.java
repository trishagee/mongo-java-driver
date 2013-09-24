/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb;

import org.bson.BSONReader;
import org.bson.BSONWriter;
import org.bson.types.ObjectId;
import org.mongodb.codecs.DocumentCodec;
import org.mongodb.json.JSONMode;
import org.mongodb.json.JSONReader;
import org.mongodb.json.JSONReaderSettings;
import org.mongodb.json.JSONWriter;
import org.mongodb.json.JSONWriterSettings;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A representation of a document as a {@code Map}.  All iterators will traverse the elements in
 * insertion order, as with {@code LinkedHashMap}.
 *
 * @mongodb.driver.manual core/document document
 * @since 3.0.0
 */
public class Document implements Map<String, Object>, Serializable {
    private static final long serialVersionUID = 6297731997167536582L;

    private final LinkedHashMap<String, Object> documentAsMap;

    /**
     * Creates an empty Document instance.
     */
    public Document() {
        documentAsMap = new LinkedHashMap<String, Object>();
    }

    /**
     * Create a Document instance initialized with the given key/value pair.
     *
     * @param key   key
     * @param value value
     */
    public Document(final String key, final Object value) {
        documentAsMap = new LinkedHashMap<String, Object>();
        documentAsMap.put(key, value);
    }

    /**
     * Creates a Document instance initialized with the given map.
     *
     * @param map initial map
     */
    public Document(final Map<String, Object> map) {
        documentAsMap = new LinkedHashMap<String, Object>(map);
    }


    /**
     * Converts a string in JSON format to a {@code Document}
     *
     * @param s document representation in JSON format that conforms <a href="http://www.json.org/">JSON RFC specifications</a>.
     * @return a corresponding {@code Document} object
     * @throws org.mongodb.json.JSONParseException
     *          if the input is invalid
     */
    public static Document valueOf(final String s) {
        return Document.valueOf(s, JSONMode.Strict);
    }

    /**
     * Converts a string in JSON format to a {@code Document}
     *
     * @param json document representation in JSON format
     * @return a corresponding {@code Document} object
     * @throws org.mongodb.json.JSONParseException
     *          if the input is invalid
     */
    public static Document valueOf(final String json, final JSONMode mode) {
        final BSONReader bsonReader = new JSONReader(new JSONReaderSettings(mode), json);
        return new DocumentCodec().decode(bsonReader);
    }

    /**
     * Put the given key/value pair into this Document and return this.  Useful for chaining puts in a single expression,
     * e.g.  {@code doc.append("a", 1).append("b", 2)}
     *
     * @param key   key
     * @param value value
     * @return this
     */
    public Document append(final String key, final Object value) {
        documentAsMap.put(key, value);
        return this;
    }

    /**
     * Gets the value of the given key, casting it to the given {@code Class<T>}.  This is useful to avoid having casts
     * in client code, though the effect is the same.  So to get the value of a key that is of type String, you would write
     * {@code String name = doc.get("name", String.class)} instead of {@code String name = (String) doc.get("x") }.
     *
     * @param key   the key
     * @param clazz the class to cast the value to
     * @param <T>   the type of the class
     * @return the value of the given key, or null if the instance does not contain this key.
     * @throws ClassCastException if the value of the given key is not of type T
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final Object key, final Class<T> clazz) {
        return (T) documentAsMap.get(key);
    }

    public Integer getInteger(final Object key) {
        return (Integer) get(key);
    }

    public Long getLong(final Object key) {
        return (Long) get(key);
    }

    public Double getDouble(final Object key) {
        return (Double) get(key);
    }

    public String getString(final Object key) {
        return (String) get(key);
    }

    public Boolean getBoolean(final Object key) {
        return (Boolean) get(key);
    }

    public ObjectId getObjectId(final Object key) {
        return (ObjectId) get(key);
    }

    public Date getDate(final Object key) {
        return (Date) get(key);
    }

    // Vanilla Map methods delegate to map field

    @Override
    public int size() {
        return documentAsMap.size();
    }

    @Override
    public boolean isEmpty() {
        return documentAsMap.isEmpty();
    }

    @Override
    public boolean containsValue(final Object value) {
        return documentAsMap.containsValue(value);
    }

    @Override
    public boolean containsKey(final Object key) {
        return documentAsMap.containsKey(key);
    }

    @Override
    public Object get(final Object key) {
        return documentAsMap.get(key);
    }

    @Override
    public Object put(final String key, final Object value) {
        return documentAsMap.put(key, value);
    }

    @Override
    public Object remove(final Object key) {
        return documentAsMap.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ?> map) {
        documentAsMap.putAll(map);
    }

    @Override
    public void clear() {
        documentAsMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return documentAsMap.keySet();
    }

    @Override
    public Collection<Object> values() {
        return documentAsMap.values();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return documentAsMap.entrySet();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Document document = (Document) o;

        if (!documentAsMap.equals(document.documentAsMap)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return documentAsMap.hashCode();
    }

    /**
     * Returns a String object representing this Document's in a <a href="http://www.json.org/">JSON RFC</a> format.
     * @return a json representation of the document.
     */
    @Override
    public String toString() {
        //TODO: WARNING - this toString will not work if the Document contains any non-standard types,
        // i.e. anything that requires a custom codec, like POJOs or custom CollectibleCodecs for generic Collections
        final StringWriter writer = new StringWriter();
        final BSONWriter bsonWriter = new JSONWriter(writer, new JSONWriterSettings(JSONMode.Strict));
        final Codec<Document> codec = new DocumentCodec();
        codec.encode(bsonWriter, this);

        return writer.toString();
    }
}
