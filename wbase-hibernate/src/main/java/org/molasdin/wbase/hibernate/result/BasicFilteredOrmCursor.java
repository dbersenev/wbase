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

package org.molasdin.wbase.hibernate.result;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.molasdin.wbase.hibernate.FilteredOrmCursor;
import org.molasdin.wbase.storage.AbstractCursor;
import org.molasdin.wbase.storage.Order;
import org.molasdin.wbase.storage.Cursor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;


public class BasicFilteredOrmCursor<T> extends AbstractCursor<T> implements FilteredOrmCursor<T> {

    private final static String FILTER_ORDER = "this.%s %s";
    private final static String FILTER_FILTER = "upper(str(this.%s)) ilike %s%%";

    private final static String ORDER_ASC = "asc";
    private final static String ORDER_DESC = "desc";

    private Collection<T> collectionProxy;
    private Object owner;
    private SessionFactory sessionFactory;

    public void setOwner(Object owner) {
        this.owner = owner;
    }

    public void setCollectionProxy(Collection<T> collectionProxy) {
        this.collectionProxy = collectionProxy;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    @Override
    public List<T> data() {
        StringBuilder builder = new StringBuilder();
        session().buildLockRequest(LockOptions.NONE).lock(owner);
        builder.append(populateFilters());
        builder.append(' ');

        List<Pair<String, Order>> orders = orders();
        if(!orders.isEmpty()){
            builder.append("order by ");
            boolean added = false;
            for(Pair<String, Order> order: orders){
                if(added){
                    builder.append(",");
                }
                builder.append(String.format(FILTER_ORDER, order.getLeft(), Order.ASC.equals(order.getRight()) ? ORDER_ASC : ORDER_DESC));
                added = true;
            }
            builder.append(' ');
        }

        Session session = session();
        return postProcessData((List<T>)session.createFilter(collectionProxy, builder.toString())
                .setFirstResult(calculatedRowOffset())
                .setMaxResults(pageSize())
                .list());
    }

    private String populateFilters(){
        StringBuilder query = new StringBuilder();
        if(filters().size() > 0){
            for(String prop: filters().keySet()){
                query.append(String.format(FILTER_FILTER, prop, filters().get(prop).getRight().toUpperCase()));
            }
        }
        return query.toString();
    }

    @Transactional
    @Override
    public long totalRecords() {
        StringBuilder builder = new StringBuilder();
        builder.append("select count(*) ");
        builder.append(populateFilters());
        Session session = session();
        session.buildLockRequest(LockOptions.NONE).lock(owner);
        return (Long)session.createFilter(collectionProxy, builder.toString()).uniqueResult();
    }

    private Session session(){
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Cursor<T> copy() {
        return null;
    }
}
