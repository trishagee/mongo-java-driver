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

package org.mongodb.codecs;

import org.bson.BSONBinaryReader;
import org.bson.BSONBinarySubType;
import org.bson.BSONReader;
import org.bson.BSONType;
import org.bson.BSONWriter;
import org.bson.ByteBufNIO;
import org.bson.io.BasicInputBuffer;
import org.bson.types.BSONTimestamp;
import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.mongodb.DBRef;
import org.mongodb.Decoder;
import org.mongodb.json.JSONReader;
import org.mongodb.json.JSONWriter;

import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.regex.Pattern;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

public class BSONCodecsTest {
    private final BSONCodecs bsonCodecs = BSONCodecs.createDefault();

    @Test
    public void shouldBeAbleToEncodeString() {
        assertThat(bsonCodecs.canEncode(String.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeObjectId() {
        assertThat(bsonCodecs.canEncode(ObjectId.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeInteger() {
        assertThat(bsonCodecs.canEncode(Integer.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeLong() {
        assertThat(bsonCodecs.canEncode(Long.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeDouble() {
        assertThat(bsonCodecs.canEncode(Double.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeBinary() {
        assertThat(bsonCodecs.canEncode(Binary.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeDate() {
        assertThat(bsonCodecs.canEncode(Date.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeTimestamp() {
        assertThat(bsonCodecs.canEncode(BSONTimestamp.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeBoolean() {
        assertThat(bsonCodecs.canEncode(Boolean.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodePattern() {
        assertThat(bsonCodecs.canEncode(Pattern.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeMinKey() {
        assertThat(bsonCodecs.canEncode(MinKey.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeMaxKey() {
        assertThat(bsonCodecs.canEncode(MaxKey.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeCode() {
        assertThat(bsonCodecs.canEncode(Code.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeNull() {
        assertThat(bsonCodecs.canEncode(null), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeFloat() {
        assertThat(bsonCodecs.canEncode(Float.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeShort() {
        assertThat(bsonCodecs.canEncode(Short.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeByte() {
        assertThat(bsonCodecs.canEncode(Byte.class), is(true));
    }

    @Test
    public void shouldBeAbleToEncodeByteArray() {
        assertThat(bsonCodecs.canEncode(byte[].class), is(true));
    }

    @Test
    public void shouldBeAbleToDecodeString() {
        assertThat(bsonCodecs.canDecode(String.class), is(true));
    }

    @Test
    public void shouldBeAbleToDecodeObjectId() {
        assertThat(bsonCodecs.canDecode(ObjectId.class), is(true));
    }

    @Test
    public void shouldBeAbleToDecodeInteger() {
        assertThat(bsonCodecs.canDecode(Integer.class), is(true));
    }

    @Test
    public void shouldBeAbleToDecodeLong() {
        assertThat(bsonCodecs.canDecode(Long.class), is(true));
    }

    @Test
    public void shouldBeAbleToDecodeDouble() {
        assertThat(bsonCodecs.canDecode(Double.class), is(true));
    }

    @Test
    public void shouldBeAbleToDecodeDate() {
        assertThat(bsonCodecs.canDecode(Date.class), is(true));
    }

    @Test
    public void shouldBeAbleToDecodeTimestamp() {
        assertThat(bsonCodecs.canDecode(BSONTimestamp.class), is(true));
    }

    @Test
    public void shouldBeAbleToDecodeBoolean() {
        assertThat(bsonCodecs.canDecode(Boolean.class), is(true));
    }

    @Test
    public void shouldBeAbleToDecodePattern() {
        assertThat(bsonCodecs.canDecode(Pattern.class), is(true));
    }

    @Test
    public void shouldBeAbleToDecodeMinKey() {
        assertThat(bsonCodecs.canDecode(MinKey.class), is(true));
    }

    @Test
    public void shouldBeAbleToDecodeMaxKey() {
        assertThat(bsonCodecs.canDecode(MaxKey.class), is(true));
    }

    @Test
    public void shouldBeAbleToDecodeCode() {
        assertThat(bsonCodecs.canDecode(Code.class), is(true));
    }

    @Test
    public void shouldBeAbleToDecodeNull() {
        assertThat(bsonCodecs.canDecode(null), is(true));
    }

    //these are classes that have encoders but not decoders, not symmetrical
    @Test
    public void shouldNotBeAbleToDecodeByteArray() {
        assertThat(bsonCodecs.canDecode(byte[].class), is(false));
    }

    @Test
    public void shouldNotBeAbleToDecodeShort() {
        assertThat(bsonCodecs.canDecode(Short.class), is(false));
    }

    @Test
    public void shouldNotBeAbleToDecodeBinary() {
        assertThat(bsonCodecs.canDecode(Binary.class), is(false));
    }

    @Test
    public void shouldNotBeAbleToDecodeFloat() {
        assertThat(bsonCodecs.canDecode(Float.class), is(false));
    }

    @Test
    public void shouldNotBeAbleToDecodeByte() {
        assertThat(bsonCodecs.canDecode(Byte.class), is(false));
    }

    @Test
    public void shouldBeAbleToDecodeDBPointer() {
        final byte[] bytes = {
                26, 0, 0, 0, 12, 97, 0, 2, 0, 0, 0, 98, 0, 82, 9, 41, 108,
                -42, -60, -29, -116, -7, 111, -1, -36, 0
        };
        final BSONReader reader = new BSONBinaryReader(
                new BasicInputBuffer(new ByteBufNIO(ByteBuffer.wrap(bytes))), true
        );

        reader.readStartDocument();
        reader.readName();

        final Object object = bsonCodecs.decode(reader);

        assertThat(object, instanceOf(DBRef.class));
        final DBRef reference = (DBRef) object;
        assertThat(reference.getRef(), is("b"));
        assertThat(reference.getId(), instanceOf(ObjectId.class));
        assertThat((ObjectId) reference.getId(), is(new ObjectId("5209296cd6c4e38cf96fffdc")));
    }

    @Test
    public void testOtherDecoderMethod() {
        @SuppressWarnings("rawtypes")
        BSONCodecs codecs = BSONCodecs.builder(bsonCodecs).otherDecoder(BSONType.BINARY, new Decoder() {
            @Override
            public Object decode(final BSONReader reader) {
                return reader.readBinaryData().getData();
            }
        }).build();
        final StringWriter stringWriter = new StringWriter();
        BSONWriter bsonWriter = new JSONWriter(stringWriter);
        final Binary binaryValue = new Binary(BSONBinarySubType.Binary, new byte[]{1, 2, 3});
        bsonWriter.writeStartDocument();
        bsonWriter.writeBinaryData("binary", binaryValue);
        bsonWriter.writeEndDocument();
        BSONReader bsonReader = new JSONReader(stringWriter.toString());
        bsonReader.readStartDocument();
        bsonReader.readName();
        assertArrayEquals(binaryValue.getData(), (byte[]) codecs.decode(bsonReader));
    }
}
