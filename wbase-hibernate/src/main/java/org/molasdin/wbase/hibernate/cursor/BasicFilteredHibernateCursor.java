/*
 * Copyright 2013 Bersenev Dmitry molasdin@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.molasdin.wbase.hibernate.cursor;

import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.molasdin.wbase.hibernate.HibernateEngine;
import org.molasdin.wbase.storage.cursor.DelegatingBiDirectionalBatchCursor;
import org.molasdin.wbase.transaction.manager.TransactionManager;

import java.util.List;
import java.util.function.Function;


public class BasicFilteredHibernateCursor<T> extends DelegatingBiDirectionalBatchCursor<T, HibernateEngine> {


    private Function<Session, Query> query;
    private Object owner;

    public BasicFilteredHibernateCursor(TransactionManager<HibernateEngine> pm, Function<Session, Query> query,
                                        Object owner, long count) {
        super(pm);
        this.query = query;
        setSize(count);
        this.owner = owner;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<T> loadTx(HibernateEngine ctx) {
        ctx.session().buildLockRequest(LockOptions.NONE).lock(owner);
        return  (List<T>)query.apply(ctx.session()).setFirstResult((int)currentOffset())
                .setMaxResults((int)pageSize())
                .list();
    }

}
