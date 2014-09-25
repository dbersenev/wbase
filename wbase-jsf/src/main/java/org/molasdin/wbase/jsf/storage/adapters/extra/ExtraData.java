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

package org.molasdin.wbase.jsf.storage.adapters.extra;

import java.util.List;


/**
 * Represents data which is added right into the table itself
 * Supports storage and creation of the new records
 * @param <T>
 */
public interface ExtraData<T> {
    /**
     * Creates empty record
     */
    void createNew();

    void acceptNew();
    /**
     * Removes empty record
     */
    void cancelNew();

    boolean hasNew();

    /**
     * In table new and previously added data
     * @return
     */
    List<T> data();

    /**
     * Check if record exists
     * @param record
     * @return
     */
    boolean contains(T record);

    void remove(T record);

    void add(T record);

    List<T> newRecords();
    List<T> recordsRemoved();
}
