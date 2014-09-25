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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class BasicOrmRepository<T> implements OrmRepository<T> {

    private Class<T> clazz;

    private OrmSupport support;

    public void setSupport(OrmSupport support) {
        this.support = support;
    }

    public BasicOrmRepository(Class<T> clazz) {
        this.clazz = clazz;
    }

    public OrmSupport support() {
        return support;
    }

    protected Class<T> clazz() {
        return clazz;
    }

    @Transactional
    @Override
    public Cursor<T> all() {
        return support().resultFactory().createSearchResult(Search.all(clazz));
    }

    @Transactional
    @Override
    @SuppressWarnings("unchecked")
    public T byId(Serializable id) {
        return (T) support.currentSession().createCriteria(clazz).add(Restrictions.idEq(id)).uniqueResult();
    }

    @Override
    public <U> Cursor<U> filteredCollection(T owner, Collection<U> collection) {
        attach(owner);
        return support().resultFactory().createCollectionSearchResult(owner, collection);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> List<U> simpleFilteredCollection(T owner, Collection<U> collection, String filter) {
        attach(owner);
        return (List<U>) support().currentSession().createFilter(collection, filter).list();
    }

    @Transactional
    @Override
    public List<T> byQuery(String query, Map<String, ?> arguments) {
        return support.queryForList(query, arguments, clazz);
    }

    @Transactional
    public List<T> findByCriteria(DetachedCriteria criteria) {
        return support.invokeCriteriaForList(criteria, clazz);
    }

    @Transactional
    @Override
    public List<T> allAtOnce(String orderProp, Order order) {
        Criteria criteria = support().currentSession().createCriteria(clazz);
        criteria.addOrder(order == Order.ASC ? org.hibernate.criterion.Order.asc(orderProp) :
                org.hibernate.criterion.Order.desc(orderProp));
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return support().invokeCriteriaForList(criteria, clazz);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void attach(T o) {
        Session session = support.currentSession();
        if(session.contains(o)){
            return;
        }
        session.buildLockRequest(LockOptions.NONE).lock(o);
    }

    @Override
    @Transactional
    public void refreshChild(T o, Object child){
        attach(o);
        Hibernate.initialize(child);
    }

    @Transactional
    @Override
    public void save(T o) {
        support().currentSession().save(o);
    }

    @Transactional
    @Override
    public void merge(T o) {
        support().currentSession().merge(o);
    }

    @Transactional
    @Override
    public void remove(T o) {
        support().currentSession().delete(o);
    }

    @Transactional
    @Override
    public void update(T o) {
        support().currentSession().update(o);
    }

    @Transactional
    @Override
    public void saveOrUpdate(T o) {
        support().currentSession().saveOrUpdate(o);
    }

    @Transactional
    @Override
    public void refresh(T o) {
        attach(o);
        support().currentSession().refresh(o);
    }
}
