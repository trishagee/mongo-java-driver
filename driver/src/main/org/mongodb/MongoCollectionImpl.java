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

import org.mongodb.command.MapReduceCommand;
import org.mongodb.connection.SingleResultCallback;
import org.mongodb.operation.CountOperation;
import org.mongodb.operation.Find;
import org.mongodb.operation.FindAndRemove;
import org.mongodb.operation.FindAndRemoveOperation;
import org.mongodb.operation.FindAndReplace;
import org.mongodb.operation.FindAndReplaceOperation;
import org.mongodb.operation.FindAndUpdate;
import org.mongodb.operation.FindAndUpdateOperation;
import org.mongodb.operation.Insert;
import org.mongodb.operation.InsertOperation;
import org.mongodb.operation.QueryOperation;
import org.mongodb.operation.Remove;
import org.mongodb.operation.RemoveOperation;
import org.mongodb.operation.Replace;
import org.mongodb.operation.ReplaceOperation;
import org.mongodb.operation.SingleResultFuture;
import org.mongodb.operation.SingleResultFutureCallback;
import org.mongodb.operation.Update;
import org.mongodb.operation.UpdateOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class MongoCollectionImpl<T> implements MongoCollection<T> {

    private final CollectionAdministration admin;
    private final MongoClientImpl client;
    private final String name;
    private final MongoDatabase database;
    private final MongoCollectionOptions options;
    private final CollectibleCodec<T> codec;

    public MongoCollectionImpl(final String name, final MongoDatabaseImpl database,
                               final CollectibleCodec<T> codec, final MongoCollectionOptions options,
                               final MongoClientImpl client) {

        this.codec = codec;
        this.name = name;
        this.database = database;
        this.options = options;
        this.client = client;
        admin = new CollectionAdministrationImpl(client, options.getBsonCodecs(), getNamespace(), getDatabase());
    }

    @Override
    public WriteResult insert(final T document) {
        return new MongoCollectionView().insert(document);
    }

    @Override
    public WriteResult insert(final List<T> documents) {
        return new MongoCollectionView().insert(documents);
    }

    @Override
    public WriteResult save(final T document) {
        return new MongoCollectionView().save(document);
    }

    @Override
    public MongoPipeline<T> pipe() {
        return new MongoCollectionPipeline();
    }

    @Override
    public CollectionAdministration tools() {
        return admin;
    }

    @Override
    public MongoView<T> find() {
        return new MongoCollectionView();
    }

    @Override
    public MongoView<T> find(final Document filter) {
        return new MongoCollectionView().find(filter);
    }

    @Override
    public MongoView<T> find(final ConvertibleToDocument filter) {
        return new MongoCollectionView().find(filter);
    }

    @Override
    public MongoView<T> withWriteConcern(final WriteConcern writeConcern) {
        return new MongoCollectionView().withWriteConcern(writeConcern);
    }

    private Codec<Document> getDocumentCodec() {
        return getOptions().getDocumentCodec();
    }

    @Override
    public MongoDatabase getDatabase() {
        return database;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CollectibleCodec<T> getCodec() {
        return codec;
    }

    @Override
    public MongoCollectionOptions getOptions() {
        return options;
    }

    @Override
    public MongoNamespace getNamespace() {
        return new MongoNamespace(getDatabase().getName(), getName());
    }

    private final class MongoCollectionView implements MongoView<T> {
        private final Find findOp;
        private WriteConcern writeConcern;
        private boolean limitSet;
        private boolean upsert;

        private MongoCollectionView() {
            findOp = new Find();
            findOp.readPreference(getOptions().getReadPreference());
            writeConcern = getOptions().getWriteConcern();
        }

        @Override
        public MongoCursor<T> iterator() {
            return get();
        }

        @Override
        public MongoView<T> find(final Document filter) {
            findOp.filter(filter);
            return this;
        }

        @Override
        public MongoView<T> find(final ConvertibleToDocument filter) {
            return find(filter.toDocument());
        }

        @Override
        public MongoView<T> sort(final ConvertibleToDocument sortCriteria) {
            return sort(sortCriteria.toDocument());
        }

        @Override
        public MongoView<T> sort(final Document sortCriteria) {
            findOp.order(sortCriteria);
            return this;
        }

        @Override
        public MongoView<T> fields(final Document selector) {
            findOp.select(selector);
            return this;
        }

        @Override
        public MongoView<T> fields(final ConvertibleToDocument selector) {
            return fields(selector.toDocument());
        }

        @Override
        public MongoView<T> upsert() {
            upsert = true;
            return this;
        }

        @Override
        public MongoView<T> withQueryOptions(final QueryOptions queryOptions) {
            findOp.options(queryOptions);
            return this;
        }

        @Override
        public MongoView<T> skip(final int skip) {
            findOp.skip(skip);
            return this;
        }

        @Override
        public MongoView<T> limit(final int limit) {
            findOp.limit(limit);
            limitSet = true;
            return this;
        }

        @Override
        public MongoView<T> withReadPreference(final ReadPreference readPreference) {
            findOp.readPreference(readPreference);
            return this;
        }

        @Override
        public MongoCursor<T> get() {
            return new QueryOperation<T>(getNamespace(), findOp, getDocumentCodec(), getCodec(), client.getBufferProvider(),
                    client.getSession(), false).execute();
        }

        @Override
        public T getOne() {
            final MongoCursor<T> cursor = new QueryOperation<T>(getNamespace(), findOp.batchSize(-1), getDocumentCodec(), getCodec(),
                    client.getBufferProvider(), client.getSession(), false).execute();

            return cursor.hasNext() ? cursor.next() : null;
        }

        @Override
        public long count() {
            return new CountOperation(getNamespace(), findOp, getDocumentCodec(), client.getBufferProvider(), client.getSession(), false)
                    .execute();
        }

        @Override
        public MongoIterable<T> mapReduce(final String map, final String reduce) {
            final MapReduceCommand commandOperation = new MapReduceCommand(findOp, getName(), map, reduce);
            final CommandResult commandResult = getDatabase().executeCommand(commandOperation.toDocument(),
                    commandOperation.getReadPreference());
            return new SingleShotCommandIterable<T>(commandResult);
        }

        @Override
        public void forEach(final Block<? super T> block) {
            final MongoCursor<T> cursor = get();
            try {
                while (cursor.hasNext()) {
                    if (!block.run(cursor.next())) {
                        break;
                    }
                }
            } finally {
                cursor.close();
            }
        }


        @Override
        public <A extends Collection<? super T>> A into(final A target) {
            forEach(new Block<T>() {
                @Override
                public boolean run(final T t) {
                    target.add(t);
                    return true;
                }
            });
            return target;
        }

        @Override
        public <U> MongoIterable<U> map(final Function<T, U> mapper) {
            return new MappingIterable<T, U>(this, mapper);
        }

        @Override
        public MongoView<T> withWriteConcern(final WriteConcern writeConcernForThisOperation) {
            writeConcern = writeConcernForThisOperation;
            return this;
        }

        @Override
        public WriteResult insert(final T document) {
            return new InsertOperation<T>(getNamespace(), new Insert<T>(writeConcern, document), getCodec(), client.getBufferProvider(),
                    client.getSession(), false).execute();
        }

        @Override
        public WriteResult insert(final List<T> documents) {
            return new InsertOperation<T>(getNamespace(), new Insert<T>(writeConcern, documents), getCodec(), client.getBufferProvider(),
                    client.getSession(), false).execute();
        }

        @Override
        public WriteResult save(final T document) {
            final Object id = getCodec().getId(document);
            if (id == null) {
                return insert(document);
            }
            else {
                return upsert().find(new Document("_id", id)).replace(document);
            }
        }

        @Override
        public WriteResult remove() {
            final Remove remove = new Remove(writeConcern, findOp.getFilter()).multi(getMultiFromLimit());
            return new RemoveOperation(getNamespace(), remove, getDocumentCodec(), client.getBufferProvider(), client.getSession(),
                    false).execute();
        }

        @Override
        public WriteResult removeOne() {
            final Remove remove = new Remove(writeConcern, findOp.getFilter()).multi(false);
            return new RemoveOperation(getNamespace(), remove, getDocumentCodec(), client.getBufferProvider(), client.getSession(),
                    false).execute();
        }

        @Override
        public WriteResult update(final Document updateOperations) {
            final Update update = new Update(writeConcern, findOp.getFilter(), updateOperations).upsert(upsert)
                    .multi(getMultiFromLimit());
            return new UpdateOperation(getNamespace(), update, getDocumentCodec(), client.getBufferProvider(), client.getSession(),
                    false).execute();
        }

        @Override
        public WriteResult update(final ConvertibleToDocument updateOperations) {
            return update(updateOperations.toDocument());
        }

        @Override
        public WriteResult updateOne(final Document updateOperations) {
            final Update update = new Update(writeConcern, findOp.getFilter(), updateOperations).upsert(upsert).multi(false);
            return new UpdateOperation(getNamespace(), update, getDocumentCodec(), client.getBufferProvider(), client.getSession(),
                    false).execute();
        }

        @Override
        public WriteResult updateOne(final ConvertibleToDocument updateOperations) {
            return updateOne(updateOperations.toDocument());
        }

        @Override
        public WriteResult replace(final T replacement) {
            final Replace<T> replace = new Replace<T>(writeConcern, findOp.getFilter(), replacement).upsert(upsert);
            return new ReplaceOperation<T>(getNamespace(), replace, getDocumentCodec(), getCodec(), client.getBufferProvider(),
                    client.getSession(), false).execute();
        }

        @Override
        public T updateOneAndGet(final Document updateOperations) {
            return updateOneAndGet(updateOperations, Get.AfterChangeApplied);
        }

        @Override
        public T updateOneAndGet(final ConvertibleToDocument updateOperations) {
            return updateOneAndGet(updateOperations.toDocument());
        }

        @Override
        public T replaceOneAndGet(final T replacement) {
            return replaceOneAndGet(replacement, Get.AfterChangeApplied);
        }

        @Override
        public T getOneAndUpdate(final Document updateOperations) {
            return updateOneAndGet(updateOperations, Get.BeforeChangeApplied);
        }

        @Override
        public T getOneAndUpdate(final ConvertibleToDocument updateOperations) {
            return getOneAndUpdate(updateOperations.toDocument());
        }

        @Override
        public T getOneAndReplace(final T replacement) {
            return replaceOneAndGet(replacement, Get.BeforeChangeApplied);
        }

        public T updateOneAndGet(final Document updateOperations, final Get beforeOrAfter) {
            final FindAndUpdate<T> findAndUpdate = new FindAndUpdate<T>().where(findOp.getFilter())
                    .updateWith(updateOperations)
                    .returnNew(asBoolean(beforeOrAfter))
                    .select(findOp.getFields())
                    .sortBy(findOp.getOrder())
                    .upsert(upsert);

            return new FindAndUpdateOperation<T>(getNamespace(), findAndUpdate, getCodec(), client.getBufferProvider(), client.getSession(),
                    false).execute();
        }

        public T replaceOneAndGet(final T replacement, final Get beforeOrAfter) {
            final FindAndReplace<T> findAndReplace = new FindAndReplace<T>(replacement).where(findOp.getFilter())
                    .returnNew(asBoolean(beforeOrAfter))
                    .select(findOp.getFields())
                    .sortBy(findOp.getOrder())
                    .upsert(upsert);
            return new FindAndReplaceOperation<T>(getNamespace(), findAndReplace, getCodec(), getCodec(), client.getBufferProvider(),
                    client.getSession(), false).execute();
        }

        @Override
        public T getOneAndRemove() {
            final FindAndRemove<T> findAndRemove = new FindAndRemove<T>().where(findOp.getFilter())
                    .select(findOp.getFields())
                    .sortBy(findOp.getOrder());

            return new FindAndRemoveOperation<T>(getNamespace(), findAndRemove, getCodec(), client.getBufferProvider(), client.getSession(),
                    false).execute();
        }

        @Override
        public MongoFuture<WriteResult> asyncReplace(final T replacement) {
            final Replace<T> replace = new Replace<T>(writeConcern, findOp.getFilter(), replacement).upsert(upsert);
            return new ReplaceOperation<T>(getNamespace(), replace, getDocumentCodec(), getCodec(), client.getBufferProvider(),
                    client.getSession(), false).executeAsync();
        }

        boolean asBoolean(final Get get) {
            return get == Get.AfterChangeApplied;
        }

        @Override
        public MongoFuture<T> asyncOne() {
            final SingleResultFuture<T> retVal = new SingleResultFuture<T>();
            new QueryOperation<T>(getNamespace(), findOp.batchSize(-1), getDocumentCodec(), getCodec(),
                    client.getBufferProvider(), client.getSession(), false).executeAsync()
                    .register(new SingleResultCallback<MongoAsyncCursor<T>>() {
                        @Override
                        public void onResult(final MongoAsyncCursor<T> cursor, final MongoException e) {
                            if (e != null) {
                                retVal.init(null, e);
                            }
                            else {
                                cursor.start(new AsyncBlock<T>() {
                                    @Override
                                    public void done() {
                                        if (!retVal.isDone()) {
                                            retVal.init(null, null);
                                        }
                                        // TODO: deal with errors
                                    }

                                    @Override
                                    public boolean run(final T t) {
                                        retVal.init(t, null);
                                        return false;
                                    }
                                });
                            }
                        }
                    });
            return retVal;
        }

        @Override
        public MongoFuture<Long> asyncCount() {
            return new CountOperation(getNamespace(), findOp, getDocumentCodec(), client.getBufferProvider(), client.getSession(), false)
                    .executeAsync();
        }

        private boolean getMultiFromLimit() {
            if (limitSet) {
                if (findOp.getLimit() == 1) {
                    return false;
                }
                else if (findOp.getLimit() == 0) {
                    return true;
                }
                else {
                    throw new IllegalArgumentException("Update currently only supports a limit of either none or 1");
                }
            }
            else {
                return true;
            }
        }

        @Override
        public void asyncForEach(final AsyncBlock<? super T> block) {
            new QueryOperation<T>(getNamespace(), findOp, getDocumentCodec(), getCodec(), client.getBufferProvider(),
                    client.getSession(), false).executeAsync().register(new SingleResultCallback<MongoAsyncCursor<T>>() {
                @Override
                public void onResult(final MongoAsyncCursor<T> cursor, final MongoException e) {
                    cursor.start(block);  // TODO: deal with exceptions
                }
            });
        }

        @Override
        public <A extends Collection<? super T>> MongoFuture<A> asyncInto(final A target) {
            final SingleResultFuture<A> future = new SingleResultFuture<A>();

            asyncInto(target, new SingleResultFutureCallback<A>(future));
            return future;
        }

        private <A extends Collection<? super T>> void asyncInto(final A target, final SingleResultCallback<A> callback) {
            asyncForEach(new AsyncBlock<T>() {
                @Override
                public void done() {
                    callback.onResult(target, null);
                }

                @Override
                public boolean run(final T t) {
                    target.add(t);
                    return true;
                }
            });
        }
    }

    private class MongoCollectionPipeline implements MongoPipeline<T> {
        private final List<Document> pipeline;

        private MongoCollectionPipeline() {
            pipeline = new ArrayList<Document>();
        }

        public MongoCollectionPipeline(final MongoCollectionPipeline from) {
            pipeline = new ArrayList<Document>(from.pipeline);
        }

        @Override
        public MongoPipeline<T> find(final Document criteria) {
            final MongoCollectionPipeline newPipeline = new MongoCollectionPipeline(this);
            newPipeline.pipeline.add(new Document("$match", criteria));
            return newPipeline;
        }

        @Override
        public MongoPipeline<T> sort(final Document sortCriteria) {
            final MongoCollectionPipeline newPipeline = new MongoCollectionPipeline(this);
            newPipeline.pipeline.add(new Document("$sort", sortCriteria));
            return newPipeline;
        }

        @Override
        public MongoPipeline<T> skip(final long skip) {
            final MongoCollectionPipeline newPipeline = new MongoCollectionPipeline(this);
            newPipeline.pipeline.add(new Document("$skip", skip));
            return newPipeline;
        }

        @Override
        public MongoPipeline<T> limit(final long limit) {
            final MongoCollectionPipeline newPipeline = new MongoCollectionPipeline(this);
            newPipeline.pipeline.add(new Document("$limit", limit));
            return newPipeline;
        }

        @Override
        public MongoPipeline<T> project(final Document projection) {
            final MongoCollectionPipeline newPipeline = new MongoCollectionPipeline(this);
            newPipeline.pipeline.add(new Document("$project", projection));
            return newPipeline;
        }

        @Override
        public MongoPipeline<T> group(final Document group) {
            final MongoCollectionPipeline newPipeline = new MongoCollectionPipeline(this);
            newPipeline.pipeline.add(new Document("$group", group));
            return newPipeline;
        }

        @Override
        public MongoPipeline<T> unwind(final String field) {
            final MongoCollectionPipeline newPipeline = new MongoCollectionPipeline(this);
            newPipeline.pipeline.add(new Document("$unwind", field));
            return newPipeline;
        }

        @Override
        public <U> MongoIterable<U> map(final Function<T, U> mapper) {
            return new MappingIterable<T, U>(this, mapper);
        }

        @Override
        public void asyncForEach(final AsyncBlock<? super T> block) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <A extends Collection<? super T>> MongoFuture<A> asyncInto(final A target) {
            throw new UnsupportedOperationException();
        }

        @Override
        @SuppressWarnings("unchecked")
        public MongoCursor<T> iterator() {
            final Document document = new Document("aggregate", getNamespace().getCollectionName()).append("pipeline", pipeline);
            final CommandResult commandResult = getDatabase().executeCommand(document, null);
            return new SingleShotCursor<T>((Iterable<T>) commandResult.getResponse().get("result"));
        }

        @Override
        public void forEach(final Block<? super T> block) {
            final MongoCursor<T> cursor = iterator();
            try {
                while (cursor.hasNext()) {
                    if (!block.run(cursor.next())) {
                        break;
                    }
                }
            } finally {
                cursor.close();
            }
        }

        @Override
        public <A extends Collection<? super T>> A into(final A target) {
            forEach(new Block<T>() {
                @Override
                public boolean run(final T t) {
                    target.add(t);
                    return true;
                }
            });
            return target;
        }
    }
}
