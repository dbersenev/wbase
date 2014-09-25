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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.molasdin.wbase.storage.Cursor;
import org.molasdin.wbase.storage.FilteringMode;
import org.molasdin.wbase.storage.Order;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import java.util.*;

/**
 * Created by dbersenev on 21.01.14.
 */
public abstract class AbstractLazyDataModelCursorWrapper<T> extends LazyDataModel<T> {

    private Cursor<T> result;
    private Map<String, SortOrder> sortOrders = new HashMap<String, SortOrder>();
    private boolean countRetrieved;
    private Map<String, Object> filters;
    private int pageNumber;
    private int pageSize;
    private List<SortMeta> defaultSort = new LinkedList<SortMeta>();

    public AbstractLazyDataModelCursorWrapper(Cursor<T> result) {
        this.result = result;
    }

    public Cursor<T> result(){
        return result;
    }

    /**
     * Set flag which control need to retrieve count from the store
     * @param countRetrieved
     */
    public void setCountRetrieved(boolean countRetrieved) {
        this.countRetrieved = countRetrieved;
    }

    public boolean isCountRetrieved(){
        return countRetrieved;
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
     * Add orders to result
     * @param multiSortMeta
     */
    public void addOrders(List<SortMeta> multiSortMeta){
        boolean multiSortEmpty = multiSortMeta == null || multiSortMeta.isEmpty();
        if (!defaultSort.isEmpty() || !multiSortEmpty){
            result.orders().clear();
            if(multiSortEmpty && !defaultSort.isEmpty()){
                multiSortMeta = defaultSort;
            }
            for(SortMeta meta: multiSortMeta){
                result.orders().add(Pair.of(meta.getSortField(), meta.getSortOrder().equals(SortOrder.ASCENDING) ?
                        Order.ASC : Order.DESC));
                if(!sortOrders.containsKey(meta.getSortField()) ||
                        !sortOrders.get(meta.getSortField()).equals(meta.getSortOrder())){
                    sortOrders.put(meta.getSortField(), meta.getSortOrder());
                    countRetrieved = false;
                }
            }
        } else{
            if(!sortOrders.isEmpty()){
                sortOrders.clear();
                countRetrieved = false;
            }
        }
    }

    /**
     * Add filters to result
     * @param stringStringMap
     */
    public void addFilters(Map<String, Object> stringStringMap){
        if (stringStringMap.size() > 0) {
            if (!stringStringMap.equals(filters)) {
                for (String key : stringStringMap.keySet()) {
                    result.filters().put(key, new ImmutablePair<FilteringMode, String>(FilteringMode.START, ObjectUtils.toString(stringStringMap.get(key))));
                }
                countRetrieved = false;
            }
        } else {
            if (filters != null && filters.size() > 0) {
                countRetrieved = false;
            }
            result.filters().clear();
        }
        filters = stringStringMap;
    }

    /**
     * Verify and save row information
     * @param row
     * @param newPageSize
     */
    public void processRowParameters(int row, int newPageSize){
        if (row != pageNumber || newPageSize != pageSize) {
            countRetrieved = false;
        }
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

    /**
     * Clone wrapper
     * @return
     */
    public LazyDataModel<T> copy(){
        return new LazyDataModelCursorWrapper<T>(result.copy());
    }

    public List<T> load(int row, int newPageSize, String property, SortOrder sortOrder, Map<String, Object> stringStringMap) {
        List<SortMeta> orders = new LinkedList<SortMeta>();
        if(property != null && sortOrder != null){
            orders.add(new SortMeta(null, property, sortOrder, null));
        }
        return load(row, newPageSize, orders, stringStringMap);
    }

}
