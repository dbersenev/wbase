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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.molasdin.wbase.hibernate.HibernateEngine;
import org.molasdin.wbase.hibernate.HibernateMatchMode;
import org.molasdin.wbase.hibernate.search.BasicQueryBuilder;
import org.molasdin.wbase.hibernate.search.QueryBuilder;
import org.molasdin.wbase.hibernate.util.HibernateUtils;
import org.molasdin.wbase.storage.FilterAndOrder;
import org.molasdin.wbase.storage.FilteringMode;
import org.molasdin.wbase.storage.Order;
import org.molasdin.wbase.storage.cursor.BiDirectionalBatchCursor;
import org.molasdin.wbase.storage.cursor.EmptyCursor;
import org.molasdin.wbase.storage.cursor.TransactionalExtBiDirectionalCursorFactory;
import org.molasdin.wbase.transaction.TransactionDescriptor;
import org.molasdin.wbase.transaction.UserTransaction;
import org.molasdin.wbase.transaction.manager.Engine;
import org.molasdin.wbase.transaction.manager.TransactionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dbersenev on 12.04.2016.
 */
public class BasicHibernateQueryCursorFactory<T> extends TransactionalExtBiDirectionalCursorFactory<T, HibernateEngine> {


    private final static String FILTER_PART = "upper(str(%s)) like upper('%s')";

    private String query;
    private String countQuery;
    private Map<String, Object> parameters = new HashMap<>();

    public BasicHibernateQueryCursorFactory(TransactionManager<HibernateEngine> pm, String query, String countQuery) {
        super(pm);
        this.query = query;
        this.countQuery = countQuery;
    }

    public void addParameter(String name, Object value){
        parameters.put(name, value);
    }
    public void addParameters(Map<String, Object> value){
        parameters.putAll(value);
    }

    @Override
    public BiDirectionalBatchCursor<T> newCursor(long pageSize) {
        long count = 0;
        try(UserTransaction<HibernateEngine> tx = processingManager().createTransaction(transactionDescriptor())) {
            Session s = tx.engine().session();
            Query cQ = buildCountQuery(s);
            Long result = (Long)cQ.uniqueResult();
            count = result != null?result:0;
            tx.commit();
        }
        if(count == 0){
            return EmptyCursor.emptyTwoWaysBatchCursor();
        }

        String q = buildQuery();
        return new BasicHibernateQueryCursor<T>(processingManager(), q, count){
            @Override
            protected TransactionDescriptor descriptor() {
                return BasicHibernateQueryCursorFactory.this.transactionDescriptor();
            }

            @Override
            public long pageSize() {
                return pageSize;
            }
        };
    }

    private String buildQuery() {
        QueryBuilder qb = new BasicQueryBuilder();
        qb.addPart(query);
        FilterAndOrder fo = filterAndOrder();
        for(String prop: fo.filters().keySet()){
            Pair<FilteringMode, String> filter = fo.filters().get(prop);
            qb.ilikeWild(prop, filter.getRight(), HibernateUtils.toMatchMode(filter.getLeft()));
        }
        return qb.query();
    }

    private Query buildCountQuery(Session session){
        QueryBuilder qb = new BasicQueryBuilder();
        qb.addPart(countQuery);
        FilterAndOrder fo = filterAndOrder();
        for(Pair<String, Order> entry: fo.orders()) {
            qb.addOrder(entry.getLeft(), entry.getRight());
        }
        return session.createQuery(qb.toString());
    }
}
