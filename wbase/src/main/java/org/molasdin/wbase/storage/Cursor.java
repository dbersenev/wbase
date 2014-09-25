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

import org.apache.commons.collections4.Closure;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;


/**
 * Result of search
 * Intended to provide convenient pagination, sorting and filtering
 * @param <T>
 */
public interface Cursor<T> {

    /**
     * Retrieve actual data
     * @return
     */
    List<T> data();

    void setCurrentOffset(int offset);
    int currentOffset();

    void setCurrentPage(int page);
    int currentPage();

    void nextPage();

    /**
     * Number of items per page
     * @param pageSize
     */
    void setPageSize(int pageSize);
    int pageSize();

    long totalRecords();

    List<Pair<String, Order>> orders();

    /**
     * Filter by properties
     * @return
     */
    Map<String, Pair<FilteringMode, String>> filters();

    Cursor<T> copy();

    void setPostProcessor(Closure<T> postProcessor);

}
