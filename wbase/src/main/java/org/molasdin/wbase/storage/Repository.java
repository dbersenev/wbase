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

package org.molasdin.wbase.storage;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface Repository<T>{

    Cursor<T> all();

    T byId(Serializable id);

    /**
     * Make search result from collection
     * @param owner
     * @param collection
     * @param <U>
     * @return
     */
    <U> Cursor<U> filteredCollection(T owner, Collection<U> collection);

    /**
     * Filter collection and return result immediately
     * @param owner
     * @param collection
     * @param filter
     * @param <U>
     * @return
     */
    <U> List<U> simpleFilteredCollection(T owner, Collection<U> collection, String filter);

    /**
     * Search by query. Query is specific to implementation
     * @param query
     * @param arguments
     * @return
     */
    List<T> byQuery(String query, Map<String, ?> arguments);

    /**
     * All records ordered by some criteria
     * @param orderProp
     * @param order
     * @return
     */
    List<T> allAtOnce(String orderProp, Order order);

    void save(T o);
    void saveOrUpdate(T o);
    void merge(T o);
    void update(T o);
    void remove(T o);

    /**
     * Refresh object fields from DB
     * @param o
     */
    void refresh(T o);

    void refreshChild(T o, Object child);
}
