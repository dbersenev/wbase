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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.molasdin.wbase.hibernate.result.BasicFilteredOrmCursor;
import org.molasdin.wbase.hibernate.result.BasicOrmCursor;
import org.molasdin.wbase.hibernate.result.BasicOrmQueryCursor;
import org.molasdin.wbase.storage.SearchConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collection;
import java.util.Map;


public class BasicOrmCursorFactory implements CursorFactory, ApplicationContextAware {

    private ApplicationContext ctx;
    private SessionFactory sessionFactory;
    private PlatformTransactionManager transactionManager;

    private String searchResultName;

    private String querySearchResultName;

    private String filteredSearchResultName;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setSearchResultName(String searchResultName) {
        this.searchResultName = searchResultName;
    }

    public void setQuerySearchResultName(String querySearchResultName) {
        this.querySearchResultName = querySearchResultName;
    }

    public void setFilteredSearchResultName(String filteredSearchResultName) {
        this.filteredSearchResultName = filteredSearchResultName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    private void initCursor(OrmCursor<?, ?> cursor) {
        cursor.setSessionFactory(sessionFactory);
        PlatformTransactionManager tmp = transactionManager;
        if(tmp == null){
            tmp = ctx.getBean("txManager", PlatformTransactionManager.class);
        }
        cursor.setTxManager(tmp);
    }

    @Override
    public <T> OrmCursor<T, DetachedCriteria> createSearchResult(SearchConfiguration<T, DetachedCriteria> spec) {
        OrmCursor<T, DetachedCriteria> tmp = newCursor();
        tmp.setSearchConfiguration(spec);
        initCursor(tmp);
        return tmp;
    }

    @Override
    public <T> OrmCursor<T, Pair<Pair<String, String>, Map<String, Object>>> createQuerySearchResult(SearchConfiguration<T,
            Pair<Pair<String, String>,
                    Map<String, Object>>> spec) {
        OrmCursor<T, Pair<Pair<String, String>, Map<String, Object>>> tmp = newQueryCursor();
        tmp.setSearchConfiguration(spec);
        initCursor(tmp);
        return tmp;
    }

    @Override
    public <T, U> FilteredOrmCursor<U> createCollectionSearchResult(T owner, Collection<U> collection) {
        FilteredOrmCursor<U> result = newFilteredCursor();
        result.setOwner(owner);
        result.setCollectionProxy(collection);
        initCursor(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> OrmCursor<T, DetachedCriteria> newCursor() {
        OrmCursor<T, DetachedCriteria> cursor = bean(searchResultName, OrmCursor.class);
        return cursor != null ? cursor : new BasicOrmCursor<T>();
    }

    @SuppressWarnings("unchecked")
    public <U> FilteredOrmCursor<U> newFilteredCursor() {
        FilteredOrmCursor<U> cursor = bean(filteredSearchResultName, FilteredOrmCursor.class);
        return cursor != null ? cursor : new BasicFilteredOrmCursor<U>();
    }

    @SuppressWarnings("unchecked")
    public <T> OrmCursor<T, Pair<Pair<String, String>, Map<String, Object>>> newQueryCursor() {
        OrmCursor<T, Pair<Pair<String, String>, Map<String, Object>>> cursor = bean(querySearchResultName, OrmCursor.class);
        return cursor != null ? cursor : new BasicOrmQueryCursor<T>();
    }

    private <U> U bean(String name, Class<U> clazz) {
        if (StringUtils.isNotBlank(name) && ctx.containsBean(name)) {
            return ctx.getBean(name, clazz);
        }
        return null;
    }
}
