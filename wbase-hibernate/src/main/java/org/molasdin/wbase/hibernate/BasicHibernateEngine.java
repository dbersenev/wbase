/*
 * Copyright 2014 Bersenev Dmitry molasdin@outlook.com
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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;

import java.util.List;
import java.util.Map;

/**
 * Created by molasdin on 10/30/14.
 */
public class BasicHibernateEngine implements HibernateEngine {

    private Session session;

    public BasicHibernateEngine(Session session) {
        this.session = session;
    }

    @Override
    public Session session() {
        return session;
    }

    @Override
    public <U> List<U> queryForList(String query, Map<String, ?> arguments, Class<U> clazz) {
        Query q = session().createQuery(query);
        addParameters(q, arguments);
        return queryForList(q, clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> List<U> queryForList(Query query, Class<U> clazz) {
        return query.list();
    }

    @Override
    public <U> List<U> invokeCriteriaForList(DetachedCriteria criteria, Class<U> clazz) {
        return invokeCriteriaForList(criteria.getExecutableCriteria(session()), clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> List<U> invokeCriteriaForList(Criteria criteria, Class<U> clazz) {
        return criteria.list();
    }

    @Override
    public <U> U queryForSingle(String query, Map<String, ?> arguments, Class<U> clazz) {
        Query q = session().createQuery(query);
        addParameters(q, arguments);
        return queryForSingle(q, clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> U queryForSingle(Query query, Class<U> clazz) {
        return (U)query.uniqueResult();
    }

    @Override
    public <U> U invokeCriteriaForSingle(DetachedCriteria criteria, Class<U> clazz) {
        return invokeCriteriaForSingle(criteria.getExecutableCriteria(session()), clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> U invokeCriteriaForSingle(Criteria criteria, Class<U> clazz) {
        return (U)criteria.uniqueResult();
    }

    private void addParameters(Query query, Map<String, ?> parameters){
        for(String key: parameters.keySet()){
            query.setParameter(key, parameters.get(key));
        }
    }

    @Override
    public void close() {

    }
}
