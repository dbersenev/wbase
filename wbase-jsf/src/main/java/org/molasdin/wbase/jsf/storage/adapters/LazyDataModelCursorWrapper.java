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

import org.molasdin.wbase.storage.MutableFilterAndOrder;
import org.molasdin.wbase.storage.cursor.ExtBiDirectionalCursorFactory;
import org.primefaces.model.SortMeta;

import java.util.List;
import java.util.Map;

/**
 * Primefaces wrapper for BiDirectionalBatchCursor
 *
 * @param <T>
 */
public class LazyDataModelCursorWrapper<T> extends AbstractLazyDataModelCursorWrapper<T> {

    public LazyDataModelCursorWrapper(ExtBiDirectionalCursorFactory<T> extBiDirectionalCursorFactory) {
        super(extBiDirectionalCursorFactory);
    }

    @Override
    public List<T> load(int row, int newPageSize, List<SortMeta> multiSortMeta, Map<String, Object> stringStringMap) {
        MutableFilterAndOrder fo = null;
        if(!multiSortMeta.isEmpty() && !stringStringMap.isEmpty()){
            fo = new MutableFilterAndOrder();
            addOrders(multiSortMeta, fo);
            addFilters(stringStringMap, fo);
        }

        if (cursor() == null || newPageSize != pageSize() || isUnstableCursor()) {
            cursorFactory().setFilterAndOrder(fo);
            setCursor(cursorFactory().newCursor(newPageSize));
        }

        if (row != pageNumber()) {
            cursor().setCurrentPage(row);
        }

        processRowParameters(row, newPageSize);

        return cursor().data();
    }

    @Override
    public int getRowCount() {
        return (int)cursor().size();
    }
}
