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

package org.mongodb.codecs

import org.bson.BSONWriter
import spock.lang.Specification
import spock.lang.Subject

class ArrayCodecWithObjectArraySpecification extends Specification {
    private final BSONWriter bsonWriter = Mock();
    private final BSONCodecs bsonCodecs = Mock();

    @Subject
    private final ArrayCodec arrayCodec = new ArrayCodec(bsonCodecs);

    def 'should write start and end for array of objects and delegate encoding of object'() {
        given:
        Object object1 = new Object();
        Object object2 = new Object();
        Object[] arrayOfObjects = [object1, object2];

        when:
        arrayCodec.encode(bsonWriter, arrayOfObjects);

        then:
        1 * bsonWriter.writeStartArray();
        then:
        1 * bsonCodecs.encode(bsonWriter, object1);
        then:
        1 * bsonCodecs.encode(bsonWriter, object2);
        then:
        1 * bsonWriter.writeEndArray();
    }

    def 'should write start & end for array of objects & delegate encoding of object when array disguised as object'() {
        given:
        Object object1 = new Object();
        Object object2 = new Object();
        Object arrayOfObjects = [object1, object2] as Object[];

        when:
        arrayCodec.encode(bsonWriter, arrayOfObjects);


        then:
        1 * bsonWriter.writeStartArray();
        then:
        1 * bsonCodecs.encode(bsonWriter, object1);
        then:
        1 * bsonCodecs.encode(bsonWriter, object2);
        then:
        1 * bsonWriter.writeEndArray();
    }

}
