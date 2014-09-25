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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BasicExtraData<T> implements ExtraData<T> {

    private List<T> data = new ArrayList<T>();

    private LazyFactory<T> lazyFactory;

    private Set<Integer> keys = new HashSet<Integer>();

    private List<T> newData = new ArrayList<T>();
    private List<T> removedData = new ArrayList<T>();

    private T newOne;

    public BasicExtraData() {
    }

    public BasicExtraData(LazyFactory<T> factory) {
        this.lazyFactory = factory;
    }

    public void createNew() {
        newOne = lazyFactory.create();
        add(newOne);
        keys.add(key(newOne));
    }

    public void cancelNew() {
        if(newOne != data.get(0)){
            newOne = null;
            return;
        }
        remove(newOne);
        newOne = null;
    }

    public void acceptNew() {
        newOne = null;
    }

    public boolean hasNew() {
        return newOne != null;
    }

    public List<T> data() {
        return data;
    }

    public boolean contains(T record) {
        return keys.contains(System.identityHashCode(record));
    }


    public void remove(T record) {
        keys.remove(key(record));
        data().remove(record);
        recordsRemoved().add(record);
    }

    public void add(T record) {
        data.add(0, record);
        keys.add(key(record));
        newData.add(0, record);
    }


    public List<T> newRecords() {
        return newData;
    }

    public List<T> recordsRemoved() {
        return removedData;
    }

    private int key(Object o){
        return System.identityHashCode(o);
    }
}

