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

package org.molasdin.wbase.jsf.storage.adapters;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.molasdin.wbase.storage.MutableFilterAndOrder;
import org.molasdin.wbase.storage.FilteringMode;
import org.molasdin.wbase.storage.Order;
import org.molasdin.wbase.storage.cursor.BiDirectionalBatchCursor;
import org.molasdin.wbase.storage.cursor.ExtBiDirectionalCursorFactory;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import java.util.*;

/**
 * Created by dbersenev on 21.01.14.
 */
public abstract class AbstractLazyDataModelCursorWrapper<T> extends LazyDataModel<T> {

    private ExtBiDirectionalCursorFactory<T> extBiDirectionalCursorFactory;
    private BiDirectionalBatchCursor<T> biDirectionalBatchCursor;
    private Map<String, SortOrder> sortOrders = new HashMap<String, SortOrder>();
    private Map<String, Object> filters;
    private int pageNumber;
    private int pageSize;
    private boolean unstableCursor = false;
    private List<SortMeta> defaultSort = new LinkedList<SortMeta>();

    public AbstractLazyDataModelCursorWrapper(ExtBiDirectionalCursorFactory<T> extBiDirectionalCursorFactory) {
        this.extBiDirectionalCursorFactory = extBiDirectionalCursorFactory;
    }


    public void setUnstableCursor(boolean unstableCursor) {
        this.unstableCursor = unstableCursor;
    }

    public boolean isUnstableCursor() {
        return unstableCursor;
    }

    public ExtBiDirectionalCursorFactory<T> cursorFactory() {
        return extBiDirectionalCursorFactory;
    }

    public BiDirectionalBatchCursor<T> cursor() {
        return biDirectionalBatchCursor;
    }

    public void setCursor(BiDirectionalBatchCursor<T> biDirectionalBatchCursor) {
        this.biDirectionalBatchCursor = biDirectionalBatchCursor;
    }

    /**
     * Set default sort order.
     * It will be applied when there are no sort options passed to the load methods
     * @param defaultSort
     */
    public void setDefaultSort(List<Pair<String, SortOrder>> defaultSort) {
        List<SortMeta> tmp = new LinkedList<SortMeta>();
        for(Pair<String, SortOrder> order: defaultSort){
            tmp.add(new SortMeta(null, order.getLeft(), order.getRight(), null));
        }
        this.defaultSort = tmp;
    }

    /**
     * Add orders to biDirectionalBatchCursor
     * @param multiSortMeta
     */
    public void addOrders(List<SortMeta> multiSortMeta, MutableFilterAndOrder fo){
        boolean multiSortEmpty = multiSortMeta == null || multiSortMeta.isEmpty();
        if (!defaultSort.isEmpty() || !multiSortEmpty){
            fo.clearOrders();
            if(multiSortEmpty && !defaultSort.isEmpty()){
                multiSortMeta = defaultSort;
            }
            for(SortMeta meta: multiSortMeta){
                fo.addOrder(meta.getSortField(), meta.getSortOrder().equals(SortOrder.ASCENDING) ?
                        Order.ASC : Order.DESC);
                if(!sortOrders.containsKey(meta.getSortField()) ||
                        !sortOrders.get(meta.getSortField()).equals(meta.getSortOrder())){
                    sortOrders.put(meta.getSortField(), meta.getSortOrder());
                    biDirectionalBatchCursor = null;
                }
            }
        } else{
            if(!sortOrders.isEmpty()){
                sortOrders.clear();
                biDirectionalBatchCursor = null;
            }
        }
    }

    /**
     * Add filters to biDirectionalBatchCursor
     * @param stringStringMap
     */
    public void addFilters(Map<String, Object> stringStringMap, MutableFilterAndOrder fo){
        if (stringStringMap.size() > 0) {
            if (!stringStringMap.equals(filters)) {
                for (String key : stringStringMap.keySet()) {
                    fo.addFilter(key, ObjectUtils.toString(stringStringMap.get(key)), FilteringMode.START);
                }
                biDirectionalBatchCursor = null;
            }
        } else {
            if (filters != null && filters.size() > 0) {
                biDirectionalBatchCursor = null;
            }
            fo.clearFilters();
        }
        filters = stringStringMap;
    }

    /**
     * Verify and save row information
     * @param row
     * @param newPageSize
     */
    public void processRowParameters(int row, int newPageSize){
        pageNumber = row;
        pageSize = newPageSize;
    }

    public int pageNumber(){
        return pageNumber;
    }

    public int pageSize(){
        return pageSize;
    }

    @Override
    public void setRowIndex(int rowIndex) {
        if(getPageSize() == 0){
            rowIndex = -1;
        }
        super.setRowIndex(rowIndex);
    }

    public List<T> load(int row, int newPageSize, String property, SortOrder sortOrder, Map<String, Object> stringStringMap) {
        List<SortMeta> orders = new LinkedList<SortMeta>();
        if(property != null && sortOrder != null){
            orders.add(new SortMeta(null, property, sortOrder, null));
        }
        return load(row, newPageSize, orders, stringStringMap);
    }

}
