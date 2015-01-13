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

import org.molasdin.wbase.storage.Cursor;
import org.molasdin.wbase.storage.Repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by dbersenev on 13.03.14.
 */
public interface HibernateRepository<T, K extends Serializable> extends Repository<T, K> {
    void attach(T o);
    /**
     * Search by query. Query is specific to implementation
     * @param query
     * @param arguments
     * @return
     */
    List<T> byQuery(String query, Map<String, ?> arguments);

    /**
     * Filter collection and return cursor immediately
     * @param owner
     * @param collection
     * @param filter
     * @param <U>
     * @return
     */
    <U> List<U> simpleFilteredCollection(T owner, Collection<U> collection, String filter);

    /**
     * Make search cursor from collection
     * @param owner
     * @param collection
     * @param <U>
     * @return
     */
    <U> Cursor<U> filteredCollection(T owner, Collection<U> collection);

    /**
     * Refresh object fields from DB
     * @param o
     */
    void refresh(T o);

    void refreshChild(T o, Object child);
}
