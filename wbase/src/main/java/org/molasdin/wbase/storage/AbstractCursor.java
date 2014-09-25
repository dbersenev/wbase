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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public abstract class AbstractCursor<T> implements Cursor<T> {

    private int currentPage;
    private int pageSize;
    private int currentOffset;
    private Closure<T> postProcessor;

    private List<Pair<String, Order>> orders = new LinkedList<Pair<String, Order>>();

    private Map<String, Pair<FilteringMode, String>> filters = new HashMap<String, Pair<FilteringMode, String>>();

    public abstract List<T> data();

    public void setCurrentOffset(int offset) {
        this.currentOffset = offset;
        this.currentPage = -1;
    }

    public int currentOffset(){
        return currentOffset;
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
        this.currentOffset = -1;
    }

    public int currentPage() {
        return currentPage;
    }

    public void nextPage() {
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int pageSize() {
        return pageSize;
    }

    public long totalRecords() {
        return 0;
    }

    public List<Pair<String, Order>> orders() {
        return orders;
    }

    public Map<String, Pair<FilteringMode, String>> filters() {
        return filters;
    }

    protected int calculatedRowOffset(){
        int leftLimit = 0;
        if(currentOffset() != -1){
            leftLimit = currentOffset();
        }else{
            leftLimit = currentPage()*pageSize();
        }
        return leftLimit;
    }

    public void setPostProcessor(Closure<T> postProcessor) {
        this.postProcessor = postProcessor;
    }

    public List<T> postProcessData(List<T> data){
        for(T entry: data){
            postProcessor.execute(entry);
        }
        return data;
    }
}
