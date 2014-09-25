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

package org.molasdin.wbase.jsf.storage.adapters;

import org.molasdin.wbase.storage.Cursor;
import org.primefaces.model.SortMeta;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Primefaces wrapper for Cursor
 *
 * @param <T>
 */
public class LazyDataModelCursorWrapper<T> extends AbstractLazyDataModelCursorWrapper<T> {


    private List<T> snapshot = Collections.emptyList();
    private int count;

    public LazyDataModelCursorWrapper(Cursor<T> result) {
        super(result);
    }

    @Override
    public List<T> load(int row, int newPageSize, List<SortMeta> multiSortMeta, Map<String, Object> stringStringMap) {
        addOrders(multiSortMeta);
        addFilters(stringStringMap);
        processRowParameters(row, newPageSize);

        if (row != pageNumber() || newPageSize != pageSize()) {
            setCountRetrieved(false);
        }

        if (isCountRetrieved() && !snapshot.isEmpty()) {
            return snapshot;
        }

        result().setPageSize(newPageSize);
        result().setCurrentOffset(row);
        snapshot = result().data();
        return snapshot;
    }

    @Override
    public int getRowCount() {
        if (!isCountRetrieved()) {
            count = (int) result().totalRecords();
            setCountRetrieved(true);
        }
        return count;
    }
}
