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
import org.molasdin.wbase.storage.Storable;
import org.molasdin.wbase.jsf.storage.adapters.extra.BasicExtraData;
import org.molasdin.wbase.jsf.storage.adapters.extra.ExtraData;
import org.molasdin.wbase.storage.cursor.ExtBiDirectionalCursorFactory;
import org.primefaces.model.SortMeta;

import java.util.*;


/**
 * Primefaces BiDirectionalBatchCursor wrapper with in-table edit support
 *
 * @param <T>
 */
public class LazyDataModelExtraCursorWrapper<T extends Storable<T>> extends AbstractLazyDataModelCursorWrapper<T> {

    private List<T> snapshot = new ArrayList<T>();
    private ExtraData<T> extraData = new BasicExtraData<T>();

    public LazyDataModelExtraCursorWrapper(ExtBiDirectionalCursorFactory<T> extBiDirectionalCursorFactory) {
        super(extBiDirectionalCursorFactory);
    }

    public LazyDataModelExtraCursorWrapper(ExtBiDirectionalCursorFactory<T> extBiDirectionalCursorFactory, ExtraData<T> extraData) {
        this(extBiDirectionalCursorFactory);
        setExtraData(extraData);
    }

    public ExtraData<T> getExtraData() {
        return extraData;
    }

    public void setExtraData(ExtraData<T> extraData) {
        this.extraData = extraData;
    }

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

        snapshot.clear();
        snapshot.addAll(cursor().data());
        //if nothing has been changed - return cached collection
        if ((extraData.recordsRemoved().isEmpty())) {
            //extra data manipulation can be done without trip to db
            List<T> newRecords = extraData.newRecords();
            if (newRecords.size() > 0) {
                snapshot.addAll(0, newRecords);
                snapshot.subList(pageSize(), snapshot.size()).clear();
                newRecords.clear();
            }
            return snapshot;
        }

        extraData.recordsRemoved().clear();
        extraData.newRecords().clear();

        //if extra data has been added and it is within selected page
        if (extraData.data().size() > 0 && (row < extraData.data().size())) {
            //how much data to display from extra data
            int dataLeft = extraData.data().size() - row;
            //right offset inside extra data
            int rightOffset = dataLeft > pageSize() ? pageSize() : dataLeft;
            //iterator with fast checks for the extra data window
            ListIterator<T> iter = extraData.data().listIterator(row);
            int nextIndex = 0;
            while (iter.hasNext() && (iter.nextIndex() < row + rightOffset)) {
                snapshot.add(nextIndex++, iter.next());
            }
            if(snapshot.size() > pageSize()) {
                snapshot.subList(pageSize(), snapshot.size()).clear();
            }
        }

        return snapshot;
    }

    @Override
    public int getRowCount() {
        return (int)cursor().size() + extraData.data().size();
    }

    public void invalidateCache() {
        snapshot.clear();
    }
}
