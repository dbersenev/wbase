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

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.criterion.DetachedCriteria;
import org.molasdin.wbase.storage.SearchConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Map;


public class BasicOrmSearchResultFactory implements SearchResultFactory, ApplicationContextAware {

    private ApplicationContext ctx;

    private String searchResultName;

    private String querySearchResultName;

    private String filteredSearchResultName;

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

    @Override
    public <T> OrmCursor<T, DetachedCriteria> createSearchResult(SearchConfiguration<T, DetachedCriteria> spec) {
        OrmCursor<T, DetachedCriteria> tmp = newSearchResult();
        tmp.setSearchConfiguration(spec);
        return tmp;
    }

    @Override
    public <T> OrmCursor<T, Pair<Pair<String, String>, Map<String, Object>>> createQuerySearchResult(SearchConfiguration<T,
                Pair<Pair<String, String>,
                        Map<String, Object>>> spec) {
        OrmCursor<T, Pair<Pair<String, String>, Map<String, Object>>> tmp = newQuerySearchResult();
        tmp.setSearchConfiguration(spec);
        return tmp;
    }

    @Override
    public <T, U> FilteredOrmCursor<U> createCollectionSearchResult(T owner, Collection<U> collection) {
        FilteredOrmCursor<U> result = newFilteredSearchResult();
        result.setOwner(owner);
        result.setCollectionProxy(collection);
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> OrmCursor<T, DetachedCriteria> newSearchResult() {
        return ctx.getBean(searchResultName, OrmCursor.class);
    }

    @SuppressWarnings("unchecked")
    public <U> FilteredOrmCursor<U> newFilteredSearchResult() {
        return ctx.getBean(filteredSearchResultName, FilteredOrmCursor.class);
    }

    @SuppressWarnings("unchecked")
    public <T> OrmCursor<T, Pair<Pair<String, String>, Map<String, Object>>> newQuerySearchResult() {
        return ctx.getBean(querySearchResultName, OrmCursor.class);
    }
}
