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

package org.mongodb.operation;

import org.mongodb.CommandResult;
import org.mongodb.Decoder;
import org.mongodb.Document;
import org.mongodb.MongoNamespace;
import org.mongodb.codecs.BSONCodecs;
import org.mongodb.codecs.DocumentCodec;
import org.mongodb.connection.BufferProvider;
import org.mongodb.protocol.CommandProtocol;
import org.mongodb.session.PrimaryServerSelector;
import org.mongodb.session.ServerConnectionProvider;
import org.mongodb.session.ServerConnectionProviderOptions;
import org.mongodb.session.Session;

import static java.lang.String.format;
import static org.mongodb.operation.DocumentHelper.putIfNotNull;
import static org.mongodb.operation.DocumentHelper.putIfTrue;

public class FindAndUpdateOperation<T> extends BaseOperation<T> {
    private final MongoNamespace namespace;
    private final FindAndUpdate<T> findAndUpdate;
    private final CommandResultWithPayloadDecoder<T> resultDecoder;
    private final DocumentCodec commandEncoder = new DocumentCodec(BSONCodecs.createDefault());

    public FindAndUpdateOperation(final MongoNamespace namespace, final FindAndUpdate<T> findAndUpdate, final Decoder<T> resultDecoder,
                                  final BufferProvider bufferProvider, final Session session, final boolean closeSession) {
        super(bufferProvider, session, closeSession);
        this.namespace = namespace;
        this.findAndUpdate = findAndUpdate;
        this.resultDecoder = new CommandResultWithPayloadDecoder<T>(resultDecoder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T execute() {
        validateUpdateDocumentToEnsureItHasUpdateOperators(findAndUpdate.getUpdateOperations());
        final ServerConnectionProvider provider = createServerConnectionProvider();
        final CommandResult commandResult = new CommandProtocol(namespace.getDatabaseName(), createFindAndUpdateDocument(),
                                                                commandEncoder, resultDecoder, getBufferProvider(),
                                                                provider.getServerDescription(), provider.getConnection(), true).execute();
        return (T) commandResult.getResponse().get("value");
    }

    private void validateUpdateDocumentToEnsureItHasUpdateOperators(final Document value) {
        for (String field : value.keySet()) {
            if (field.startsWith("$")) {
                return;
            }
        }
        throw new IllegalArgumentException(format("Find and update requires an update operator (beginning with '$') in the update "
                                                  + "Document: %s", value));
    }

    private Document createFindAndUpdateDocument() {
        final Document command = new Document("findandmodify", namespace.getCollectionName());
        putIfNotNull(command, "query", findAndUpdate.getFilter());
        putIfNotNull(command, "fields", findAndUpdate.getSelector());
        putIfNotNull(command, "sort", findAndUpdate.getSortCriteria());
        putIfTrue(command, "new", findAndUpdate.isReturnNew());
        putIfTrue(command, "upsert", findAndUpdate.isUpsert());

        command.put("update", findAndUpdate.getUpdateOperations());
        return command;
    }

    private ServerConnectionProvider createServerConnectionProvider() {
        return getSession().createServerConnectionProvider(new ServerConnectionProviderOptions(false, new PrimaryServerSelector()));
    }
}
