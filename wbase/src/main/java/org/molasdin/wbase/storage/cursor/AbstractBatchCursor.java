/*
 * Copyright 2016 Bersenev Dmitry molasdin@outlook.com
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

package org.molasdin.wbase.storage.cursor;

import java.util.*;

/**
 * Created by molasdin on 12/16/15.
 */
public abstract class AbstractBatchCursor<T> implements BatchCursor<T> {
    private long pageSize;

    private List<T> data = Collections.emptyList();

    private long totalSize = -1;
    private long currentPage = 0;

    private boolean loaded = false;
    private boolean exhausted = false;

    public AbstractBatchCursor() {
    }

    public AbstractBatchCursor(long pageSize) {
        this.pageSize = pageSize;
    }

    public AbstractBatchCursor(long pageSize, long totalSize) {
        this.pageSize = pageSize;
        this.totalSize = totalSize;
    }

    protected abstract void load();

    protected boolean isExhausted() {
        return exhausted;
    }
    protected void setExhausted(boolean exhausted) {
        this.exhausted = exhausted;
        if(exhausted) {
            setData(Collections.emptyList());
        }
    }

    protected boolean isLoaded() {
        return loaded;
    }
    protected void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public long totalPages(){
        return size() == -1 ? -1: size()/pageSize();
    }

    protected void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }
    @Override
    public long pageSize() {
        return pageSize;
    }

    protected void setCurrentPageValue(long page) {
        if(page < 0 || page >= totalPages()) {
            throw new IndexOutOfBoundsException("page >= totalPages()");
        }
        if(page == currentPageValue()) {
            return;
        }
        this.currentPage = page;
        checkingLoad();
    }
    protected long currentPageValue() {
        return currentPage;
    }

    protected long currentOffset() {
        return currentPageValue() * pageSize();
    }

    @Override
    public long size() {
        return totalSize;
    }
    protected void setSize(long size){
        this.totalSize = size;
    }

    public void next() {
        this.currentPage = currentPageValue() + 1;
        checkingLoad();
    }

    @Override
    public boolean isAfter() {
        return size() == 0 || (size() != -1 && currentPageValue() >= totalPages());
    }

    protected void setData(List<T> data){
        this.data = data;
    }

    public List<T> data(){
        if(!isLoaded()) {
            checkingLoad();
        }
        return data;
    }

    @Override
    public void close() {

    }

    protected void checkingLoad(){
        if(!isAfter() && !isExhausted()) {
            load();
        } else {
            setData(Collections.emptyList());
        }
        setLoaded(true);
    }
}
