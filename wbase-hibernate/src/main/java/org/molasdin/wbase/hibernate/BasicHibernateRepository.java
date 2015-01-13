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

package org.molasdin.wbase.hibernate;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.molasdin.wbase.hibernate.search.Search;
import org.molasdin.wbase.storage.Order;
import org.molasdin.wbase.storage.Cursor;
import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.TransactionIsolation;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class BasicHibernateRepository<T, K extends Serializable> implements HibernateRepository<T, K> {

    private Class<T> clazz;

    private HibernateSupport support;

    public void setSupport(HibernateSupport support) {
        this.support = support;
    }

    public BasicHibernateRepository(Class<T> clazz) {
        this.clazz = clazz;
    }

    public HibernateSupport support() {
        return support;
    }

    protected Class<T> clazz() {
        return clazz;
    }

    @Override
    public Cursor<T> all() {
        return support().resultFactory().createSearchResult(Search.all(clazz));
    }

    @Override
    @SuppressWarnings("unchecked")
    public T byId(final K id) {
        return support().run(new org.molasdin.wbase.transaction.Transactional<HibernateEngine, T>() {
            @Override
            public T run(Transaction<HibernateEngine> tx) throws Exception {
                Session session = tx.engine().session();
                return (T)session.createCriteria(clazz).add(Restrictions.idEq(id)).uniqueResult();
            }
        });
    }

    @Override
    public <U> Cursor<U> filteredCollection(T owner, Collection<U> collection) {
        attach(owner);
        return support().resultFactory().createCollectionSearchResult(owner, collection);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> List<U> simpleFilteredCollection(final T owner, final Collection<U> collection, final String filter) {
        return support().run(new org.molasdin.wbase.transaction.Transactional<HibernateEngine, List<U>>() {
            @Override
            public List<U> run(Transaction<HibernateEngine> tx) throws Exception {
                Session session = tx.engine().session();
                attachRaw(owner, session);
                return (List<U>) session.createFilter(collection, filter).list();
            }
        });
    }

    @Override
    public List<T> byQuery(final String query, final Map<String, ?> arguments) {
        return support().run(new org.molasdin.wbase.transaction.Transactional<HibernateEngine, List<T>>() {
            @Override
            public List<T> run(Transaction<HibernateEngine> tx) throws Exception {
                return tx.engine().queryForList(query, arguments, clazz);
            }
        });
    }

    public List<T> findByCriteria(final DetachedCriteria criteria) {
        return support().run(new org.molasdin.wbase.transaction.Transactional<HibernateEngine, List<T>>() {
            @Override
            public List<T> run(Transaction<HibernateEngine> tx) throws Exception {
                return tx.engine().invokeCriteriaForList(criteria, clazz);
            }
        });
    }

    @Override
    public List<T> allAtOnce(final String orderProp, final Order order) {
        return support().run(new org.molasdin.wbase.transaction.Transactional<HibernateEngine, List<T>>() {
            @Override
            public List<T> run(Transaction<HibernateEngine> tx) throws Exception {
                Criteria criteria = tx.engine().session().createCriteria(clazz);
                criteria.addOrder(order == Order.ASC ? org.hibernate.criterion.Order.asc(orderProp) :
                        org.hibernate.criterion.Order.desc(orderProp));
                criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
                return tx.engine().invokeCriteriaForList(criteria, clazz);
            }
        });

    }

    @Override
    public void attach(final T o) {
        support().runWithIsolation(new org.molasdin.wbase.transaction.Transactional<HibernateEngine, Void>() {
            @Override
            public Void run(Transaction<HibernateEngine> tx) throws Exception {
                Session session = tx.engine().session();
                attachRaw(o, session);
                return null;
            }
        }, TransactionIsolation.READ_UNCOMMITTED);
    }

    private void attachRaw(T o, Session session){
        if(session.contains(o)){
            return;
        }
        session.buildLockRequest(LockOptions.NONE).lock(o);
    }

    @Override
    public void refreshChild(final T o, final Object child){
        support().run(new org.molasdin.wbase.transaction.Transactional<HibernateEngine, Void>() {
            @Override
            public Void run(Transaction<HibernateEngine> tx) throws Exception {
                attachRaw(o, tx.engine().session());
                Hibernate.initialize(child);
                return null;
            }
        });
    }

    @Override
    public void save(final T o) {
        support().run(new org.molasdin.wbase.transaction.Transactional<HibernateEngine, Void>() {
            @Override
            public Void run(Transaction<HibernateEngine> tx) throws Exception {
                tx.engine().session().save(o);
                return null;
            }
        });
    }

    @Override
    public void merge(final T o) {
        support().run(new org.molasdin.wbase.transaction.Transactional<HibernateEngine, Void>() {
            @Override
            public Void run(Transaction<HibernateEngine> tx) throws Exception {
                tx.engine().session().merge(o);
                return null;
            }
        });
    }

    @Override
    public void remove(final T o) {
        support().run(new org.molasdin.wbase.transaction.Transactional<HibernateEngine, Void>() {
            @Override
            public Void run(Transaction<HibernateEngine> tx) throws Exception {
                tx.engine().session().delete(o);
                return null;
            }
        });
    }

    @Override
    public void update(final T o) {
        support().run(new org.molasdin.wbase.transaction.Transactional<HibernateEngine, Void>() {
            @Override
            public Void run(Transaction<HibernateEngine> tx) throws Exception {
                tx.engine().session().update(o);
                return null;
            }
        });
    }

    @Override
    public void saveOrUpdate(final T o) {
        support().run(new org.molasdin.wbase.transaction.Transactional<HibernateEngine, Void>() {
            @Override
            public Void run(Transaction<HibernateEngine> tx) throws Exception {
                tx.engine().session().saveOrUpdate(o);
                return null;
            }
        });
    }

    @Override
    public void refresh(final T o) {
        support().run(new org.molasdin.wbase.transaction.Transactional<HibernateEngine, Void>() {
            @Override
            public Void run(Transaction<HibernateEngine> tx) throws Exception {
                Session session = tx.engine().session();
                attachRaw(o, session);
                session.refresh(o);
                return null;
            }
        });
    }
}
