/*
 * Copyright 2016 Bersenev Dmitry molasdin@outlook.com
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

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.molasdin.wbase.hibernate.HibernateEngine;
import org.molasdin.wbase.storage.Order;
import org.molasdin.wbase.storage.cursor.EmptyCursor;
import org.molasdin.wbase.storage.cursor.TransactionalExtBiDirectionalCursorFactory;
import org.molasdin.wbase.storage.cursor.BiDirectionalBatchCursor;
import org.molasdin.wbase.transaction.UserTransaction;
import org.molasdin.wbase.transaction.manager.TransactionManager;
import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.TransactionDescriptor;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Created by dbersenev on 11.03.2016.
 */
public class FilteredHibernateCursorFactory<T> extends TransactionalExtBiDirectionalCursorFactory<T, HibernateEngine> {

    private final static String FILTER_ORDER = "this.%s %s";
    private final static String FILTER_FILTER = "upper(str(this.%s)) ilike %s%%";

    private final static String ORDER_ASC = "asc";
    private final static String ORDER_DESC = "desc";

    private Collection<T> collectionProxy;
    private Object owner;

    public FilteredHibernateCursorFactory(TransactionManager<HibernateEngine> pm, Collection<T> collectionProxy, Object owner) {
        super(pm);
        this.collectionProxy = collectionProxy;
        this.owner = owner;
    }

    public FilteredHibernateCursorFactory(TransactionManager<HibernateEngine> pm, TransactionDescriptor desc, Collection<T> collectionProxy, Object owner) {
        super(pm, desc);
        this.collectionProxy = collectionProxy;
        this.owner = owner;
    }

    @Override
    public BiDirectionalBatchCursor<T> newCursor(long pageSize) {
        Function<Session, Query> q = query();
        long count = 0;
        try(UserTransaction<HibernateEngine> tx = processingManager().createTransaction(transactionDescriptor())) {
            tx.engine().session().buildLockRequest(LockOptions.NONE).lock(owner);
            Query cQ = tx.engine().session().createFilter(collectionProxy, "select count(*) " + populateFilters());
            Long result = (Long)cQ.uniqueResult();
            count = result != null?result:0;
            tx.commit();
        }
        if(count == 0){
            return EmptyCursor.emptyTwoWaysBatchCursor();
        }
        return new BasicFilteredHibernateCursor<T>(processingManager(), q, owner, count) {
            @Override
            protected TransactionDescriptor descriptor() {
                return FilteredHibernateCursorFactory.this.transactionDescriptor();
            }

            @Override
            public long pageSize() {
                return pageSize;
            }
        };
    }

    private Function<Session, Query> query() {
        StringBuilder builder = new StringBuilder();
        builder.append(populateFilters());
        builder.append(' ');

        List<Pair<String, Order>> orders = filterAndOrder().orders();
        if (!orders.isEmpty()) {
            builder.append("order by ");
            boolean added = false;
            for (Pair<String, Order> order : orders) {
                if (added) {
                    builder.append(",");
                }
                builder.append(String.format(FILTER_ORDER, order.getLeft(), Order.ASC.equals(order.getRight()) ? ORDER_ASC : ORDER_DESC));
                added = true;
            }
            builder.append(' ');
        }

        return (s) -> s.createFilter(collectionProxy, builder.toString());
    }

    private String populateFilters() {
        StringBuilder query = new StringBuilder();
        if (filterAndOrder().filters().size() > 0) {
            for (String prop : filterAndOrder().filters().keySet()) {
                query.append(String.format(FILTER_FILTER, prop, filterAndOrder().filters().get(prop).getRight().toUpperCase()));
            }
        }
        return query.toString();
    }
}
