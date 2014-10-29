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
import org.molasdin.wbase.hibernate.result.BasicFilteredHibernateCursor;
import org.molasdin.wbase.hibernate.result.BasicHibernateCursor;
import org.molasdin.wbase.hibernate.result.BasicHibernateQueryCursor;
import org.molasdin.wbase.storage.SearchConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collection;
import java.util.Map;


public class BasicHibernateCursorFactory implements HibernateCursorFactory{

    private HibernateSupport support;

    public BasicHibernateCursorFactory(HibernateSupport support) {
        this.support = support;
    }

    public void setSupport(HibernateSupport support) {
        this.support = support;
    }

    @Override
    public <T> HibernateCursor<T, DetachedCriteria> createSearchResult(SearchConfiguration<T, DetachedCriteria> spec) {
        HibernateCursor<T, DetachedCriteria> tmp = newCursor();
        tmp.setSearchConfiguration(spec);
        return tmp;
    }

    @Override
    public <T> HibernateCursor<T, Pair<Pair<String, String>, Map<String, Object>>> createQuerySearchResult(SearchConfiguration<T,
            Pair<Pair<String, String>,
                    Map<String, Object>>> spec) {
        HibernateCursor<T, Pair<Pair<String, String>, Map<String, Object>>> tmp = newQueryCursor();
        tmp.setSearchConfiguration(spec);
        return tmp;
    }

    @Override
    public <T, U> FilteredHibernateCursor<U> createCollectionSearchResult(T owner, Collection<U> collection) {
        FilteredHibernateCursor<U> result = newFilteredCursor();
        result.setOwner(owner);
        result.setCollectionProxy(collection);
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> HibernateCursor<T, DetachedCriteria> newCursor() {
        return new BasicHibernateCursor<T>(support);
    }

    @SuppressWarnings("unchecked")
    public <U> FilteredHibernateCursor<U> newFilteredCursor() {
        return new BasicFilteredHibernateCursor<U>(support);
    }

    @SuppressWarnings("unchecked")
    public <T> HibernateCursor<T, Pair<Pair<String, String>, Map<String, Object>>> newQueryCursor() {
        return new BasicHibernateQueryCursor<T>(support);
    }
}
