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

import org.molasdin.wbase.storage.Storable;
import org.molasdin.wbase.storage.Cursor;
import org.molasdin.wbase.jsf.storage.adapters.extra.BasicExtraData;
import org.molasdin.wbase.jsf.storage.adapters.extra.ExtraData;
import org.primefaces.model.SortMeta;

import java.util.*;


/**
 * Primefaces Cursor wrapper with in-table edit support
 *
 * @param <T>
 */
public class LazyDataModelExtraCursorWrapper<T extends Storable<T>> extends AbstractLazyDataModelCursorWrapper<T> {

    private List<T> snapshot = new ArrayList<T>();
    private long count;
    private ExtraData<T> extraData = new BasicExtraData<T>();

    public LazyDataModelExtraCursorWrapper(Cursor<T> result) {
        super(result);
    }

    public LazyDataModelExtraCursorWrapper(Cursor<T> result, ExtraData<T> extraData) {
        this(result);
        setExtraData(extraData);
    }

    public ExtraData<T> getExtraData() {
        return extraData;
    }

    public void setExtraData(ExtraData<T> extraData) {
        this.extraData = extraData;
    }

    public List<T> load(int row, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> stringStringMap) {
        addOrders(multiSortMeta);

        addFilters(stringStringMap);

        processRowParameters(row, pageSize);

        //if nothing has been changed - return cached collection
        if (isCountRetrieved() && !snapshot.isEmpty() &&
                (extraData.recordsRemoved().isEmpty())) {
            //extra data manipulation cab be done without trip to db
            List<T> newRecords = extraData.newRecords();
            if (newRecords.size() > 0) {
                snapshot.addAll(0, newRecords);
                int index = snapshot.size();
                while (snapshot.size() > pageSize) {
                    snapshot.remove(index - 1);
                    index--;
                }
                count += newRecords.size();
                newRecords.clear();
            }
            return snapshot;
        }

        extraData.recordsRemoved().clear();
        extraData.newRecords().clear();

        snapshot = new ArrayList<T>();
        //if extra data has been added and it is within selected page
        if (extraData.data().size() > 0 && (row < extraData.data().size())) {
            //how much data to display from extra data
            int dataLeft = extraData.data().size() - row;
            //right offset inside extra data
            int rightOffset = dataLeft > pageSize ? pageSize : dataLeft;
            //iterator with fast checks for the extra data window
            ListIterator<T> iter = extraData.data().listIterator(row);
            int nextIndex = 0;
            while (iter.hasNext() && (iter.nextIndex() < row + rightOffset)) {
                snapshot.add(nextIndex++, iter.next());
            }
            //decrease page size for the db part
            pageSize = pageSize - (rightOffset - row);
            //always starting with 0 in this case
            row = 0;
            //count refresh without trip to db
            count = snapshot.size() + (extraData.data().size() - snapshot.size());
        } else {
            //determine how much of page is consumed by extra data
            row -= extraData.data().size();
        }

        if (pageSize > 0) {
            setCountRetrieved(false);
            result().setPageSize(pageSize);
            result().setCurrentOffset(row);
            snapshot.addAll(result().data());
        }

        return snapshot;
    }

    @Override
    public int getRowCount() {
        if (!isCountRetrieved()) {
            count = result().totalRecords();
            count += extraData.data().size();
            setCountRetrieved(true);
        }
        return (int) count;
    }

    @Override
    public void setRowCount(int rowCount) {
        super.setRowCount(rowCount);
        count = rowCount;
        setCountRetrieved(true);
    }

    public void invalidateCache() {
        snapshot.clear();
    }
}
