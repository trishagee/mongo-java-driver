/**
 * Copyright (c) 2008 - 2011 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.mongodb;

import org.bson.BSONObject;
import org.bson.BasicBSONEncoder;
import org.bson.io.OutputBuffer;
import org.bson.options.BSONOptions;
import org.bson.options.JavaLegacyUUIDPolicy;
import org.bson.types.ObjectId;

import static org.bson.BSON.EOO;
import static org.bson.BSON.OBJECT;
import static org.bson.BSON.REF;


public class DefaultDBEncoder extends BasicBSONEncoder implements DBEncoder {

    public int writeObject( OutputBuffer buf, BSONObject o ){
        set( buf );
        int x = super.putObject( o );
        done();
        return x;
    }

    static class DefaultFactory implements DBEncoderFactory {
        @Override
        public DBEncoder create() {
            return new DefaultDBEncoder(JavaLegacyUUIDPolicy.INSTANCE);
        }
    }

    static class BSONOptionsEncoderFactory implements DBEncoderFactory {
        private final BSONOptions options;

        public BSONOptionsEncoderFactory(final BSONOptions options) {
            this.options = options;
        }

        @Override
        public DBEncoder create() {
            return new DefaultDBEncoder(options);
        }
    }

    @SuppressWarnings("deprecation")
    protected boolean putSpecial( String name , Object val ){
        if ( val instanceof DBPointer ){
            DBPointer r = (DBPointer)val;
            putDBPointer( name , r._ns , (ObjectId)r._id );
            return true;
        }

        if ( val instanceof DBRefBase ){
            putDBRef( name, (DBRefBase)val );
            return true;
        }

        return false;
    }

    protected void putDBPointer( String name , String ns , ObjectId oid ){
        _put( REF , name );

        _putValueString( ns );
        _buf.writeInt( oid._time() );
        _buf.writeInt( oid._machine() );
        _buf.writeInt( oid._inc() );
    }

    protected void putDBRef( String name, DBRefBase ref ){
        _put( OBJECT , name );
        final int sizePos = _buf.getPosition();
        _buf.writeInt( 0 );

        _putObjectField( "$ref" , ref.getRef() );
        _putObjectField( "$id" , ref.getId() );

        _buf.write( EOO );
        _buf.writeInt( sizePos , _buf.getPosition() - sizePos );
    }

    public static DBEncoderFactory FACTORY = new DefaultFactory();

    public DefaultDBEncoder() {
        super();
    }

    public DefaultDBEncoder(final BSONOptions _options) {
        super(_options);
    }
}
