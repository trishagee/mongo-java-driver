package org.mongodb.codecs

import org.bson.BSONBinaryReader
import org.bson.BSONBinarySubType
import org.bson.BSONReader
import org.bson.BSONType
import org.bson.BSONWriter
import org.bson.ByteBufNIO
import org.bson.io.BasicInputBuffer
import org.bson.types.BSONTimestamp
import org.bson.types.Binary
import org.bson.types.Code
import org.bson.types.MaxKey
import org.bson.types.MinKey
import org.bson.types.ObjectId
import org.mongodb.Codec
import org.mongodb.DBRef
import org.mongodb.Decoder
import org.mongodb.json.JSONReader
import org.mongodb.json.JSONWriter
import spock.lang.Specification
import spock.lang.Subject

import java.nio.ByteBuffer
import java.util.regex.Pattern

import static org.bson.BSONType.DATE_TIME

class BSONCodecsSpecification extends Specification {
    private final BSONCodecs bsonCodecs = BSONCodecs.createDefault();

    def 'shouldBeAbleToEncodeString'() {
        expect:
        bsonCodecs.canEncode(String)
    }

    def 'shouldBeAbleToEncodeObjectId'() {
        expect:
        bsonCodecs.canEncode(ObjectId)
    }

    def 'shouldBeAbleToEncodeInteger'() {
        expect:
        bsonCodecs.canEncode(Integer)
    }

    def 'shouldBeAbleToEncodeLong'() {
        expect:
        bsonCodecs.canEncode(Long)
    }

    def 'shouldBeAbleToEncodeDouble'() {
        expect:
        bsonCodecs.canEncode(Double)
    }

    def 'shouldBeAbleToEncodeBinary'() {
        expect:
        bsonCodecs.canEncode(Binary)
    }

    def 'shouldBeAbleToEncodeDate'() {
        expect:
        bsonCodecs.canEncode(Date)
    }

    def 'shouldBeAbleToEncodeTimestamp'() {
        expect:
        bsonCodecs.canEncode(BSONTimestamp)
    }

    def 'shouldBeAbleToEncodeBoolean'() {
        expect:
        bsonCodecs.canEncode(Boolean)
    }

    def 'shouldBeAbleToEncodePattern'() {
        expect:
        bsonCodecs.canEncode(Pattern)
    }

    def 'shouldBeAbleToEncodeMinKey'() {
        expect:
        bsonCodecs.canEncode(MinKey)
    }

    def 'shouldBeAbleToEncodeMaxKey'() {
        expect:
        bsonCodecs.canEncode(MaxKey)
    }

    def 'shouldBeAbleToEncodeCode'() {
        expect:
        bsonCodecs.canEncode(Code)
    }

    def 'shouldBeAbleToEncodeNull'() {
        expect:
        bsonCodecs.canEncode(null)
    }

    def 'shouldBeAbleToEncodeFloat'() {
        expect:
        bsonCodecs.canEncode(Float)
    }

    def 'shouldBeAbleToEncodeShort'() {
        expect:
        bsonCodecs.canEncode(Short)
    }

    def 'shouldBeAbleToEncodeByte'() {
        expect:
        bsonCodecs.canEncode(Byte)
    }

    def 'shouldBeAbleToEncodeByteArray'() {
        expect:
        bsonCodecs.canEncode(byte[])
    }

    def 'shouldBeAbleToDecodeString'() {
        expect:
        bsonCodecs.canDecode(String)
    }

    def 'shouldBeAbleToDecodeObjectId'() {
        expect:
        bsonCodecs.canDecode(ObjectId)
    }

    def 'shouldBeAbleToDecodeInteger'() {
        expect:
        bsonCodecs.canDecode(Integer)
    }

    def 'shouldBeAbleToDecodeLong'() {
        expect:
        bsonCodecs.canDecode(Long)
    }

    def 'shouldBeAbleToDecodeDouble'() {
        expect:
        bsonCodecs.canDecode(Double)
    }

    def 'shouldBeAbleToDecodeDate'() {
        expect:
        bsonCodecs.canDecode(Date)
    }

    def 'shouldBeAbleToDecodeTimestamp'() {
        expect:
        bsonCodecs.canDecode(BSONTimestamp)
    }

    def 'shouldBeAbleToDecodeBoolean'() {
        expect:
        bsonCodecs.canDecode(Boolean)
    }

    def 'shouldBeAbleToDecodePattern'() {
        expect:
        bsonCodecs.canDecode(Pattern)
    }

    def 'shouldBeAbleToDecodeMinKey'() {
        expect:
        bsonCodecs.canDecode(MinKey)
    }

    def 'shouldBeAbleToDecodeMaxKey'() {
        expect:
        bsonCodecs.canDecode(MaxKey)
    }

    def 'shouldBeAbleToDecodeCode'() {
        expect:
        bsonCodecs.canDecode(Code)
    }

    def 'shouldBeAbleToDecodeNull'() {
        expect:
        bsonCodecs.canDecode(null)
    }

    //these are classes that have encoders but not decoders, not symmetrical
    def 'shouldNotBeAbleToDecodeByteArray'() {
        expect:
        !bsonCodecs.canDecode(byte[])
    }

    def 'shouldNotBeAbleToDecodeShort'() {
        expect:
        !bsonCodecs.canDecode(Short)
    }

    def 'shouldNotBeAbleToDecodeBinary'() {
        expect:
        !bsonCodecs.canDecode(Binary)
    }

    def 'shouldNotBeAbleToDecodeFloat'() {
        expect:
        !bsonCodecs.canDecode(Float)
    }

    def 'shouldNotBeAbleToDecodeByte'() {
        expect:
        !bsonCodecs.canDecode(Byte)
    }

    def 'shouldBeAbleToDecodeDBPointer'() {
        given:
        final byte[] bytes = [
                26, 0, 0, 0, 12, 97, 0, 2, 0, 0, 0, 98, 0, 82, 9, 41, 108,
                -42, -60, -29, -116, -7, 111, -1, -36, 0
        ];
        final BSONReader reader = new BSONBinaryReader(
                new BasicInputBuffer(new ByteBufNIO(ByteBuffer.wrap(bytes))), true
        );

        reader.readStartDocument();
        reader.readName();

        when:
        final Object object = bsonCodecs.decode(reader);

        then:
        object instanceof DBRef;
        final DBRef reference = (DBRef) object;
        reference.getRef() == 'b';
        reference.getId() instanceof ObjectId;
        reference.getId() == new ObjectId('5209296cd6c4e38cf96fffdc');
    }

    def 'should be able to override only DateCodec and have the new codec used during encoding'() {
        given:
        Codec dateCodec = Mock()
        dateCodec.getEncoderClass() >> { Date }

        BSONWriter writer = Mock()
        Date dateToEncode = new Date()

        final BSONCodecs.Builder builderWithDefaults = BSONCodecs.builder().initialiseWithDefaults();
        builderWithDefaults.dateCodec(dateCodec);
        @Subject
        final BSONCodecs codecs = builderWithDefaults.build();

        when:
        codecs.encode(writer, dateToEncode);

        then:
        1 * dateCodec.encode(writer, dateToEncode)
    }

    def 'should be able to specify encoding a different Date type'() {
        given:
        Codec dateCodec = Mock()
        dateCodec.getEncoderClass() >> { MyDate }

        BSONWriter writer = Mock()
        MyDate dateToEncode = new MyDate()

        final BSONCodecs.Builder builderWithDefaults = BSONCodecs.builder().initialiseWithDefaults();
        builderWithDefaults.dateCodec(dateCodec);
        @Subject
        final BSONCodecs codecs = builderWithDefaults.build();

        when:
        codecs.encode(writer, dateToEncode);

        then:
        1 * dateCodec.encode(writer, dateToEncode)
    }

    def 'should be able to override only DateCodec and have the new codec used during decoding'() {
        given:
        Codec dateCodec = Mock()

        BSONReader reader = Mock()
        reader.getCurrentBSONType() >> { DATE_TIME }

        final BSONCodecs.Builder builderWithDefaults = BSONCodecs.builder().initialiseWithDefaults();
        builderWithDefaults.dateCodec(dateCodec);
        @Subject
        final BSONCodecs codecs = builderWithDefaults.build();

        when:
        codecs.decode(reader);

        then:
        1 * dateCodec.decode(reader)
    }

    def 'shouldBeAbleToSetOtherDecoder'() {
        given:
        @SuppressWarnings('rawtypes')
        final BSONCodecs codecs = BSONCodecs.builder(bsonCodecs).otherDecoder(BSONType.BINARY, new Decoder() {
            @Override
            Object decode(final BSONReader reader) {
                reader.readBinaryData().getData();
            }
        })                                  .build();
        final StringWriter stringWriter = new StringWriter();
        final BSONWriter bsonWriter = new JSONWriter(stringWriter);
        final Binary binaryValue = new Binary(BSONBinarySubType.Binary, [1, 2, 3] as byte[]);
        bsonWriter.writeStartDocument();
        bsonWriter.writeBinaryData('binary', binaryValue);
        bsonWriter.writeEndDocument();
        final BSONReader bsonReader = new JSONReader(stringWriter.toString());
        bsonReader.readStartDocument();
        bsonReader.readName();

        when:
        byte[] decoded = (byte[]) codecs.decode(bsonReader)

        then:
        binaryValue.getData() == decoded;
    }

    class MyDate { }
}
