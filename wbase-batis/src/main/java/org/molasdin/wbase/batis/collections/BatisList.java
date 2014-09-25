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

package org.molasdin.wbase.batis.collections;

import org.molasdin.wbase.collections.list.ExtendedList;
import org.molasdin.wbase.storage.Cursor;
import org.molasdin.wbase.storage.Order;

import java.util.AbstractList;

/**
 * Created by dbersenev on 17.04.2014.
 */
public class BatisList<T> extends AbstractList<T> implements ExtendedList<T> {

    public BatisList() {
    }

    @Override
    public T get(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void sort(Order order) {

    }

    @Override
    public Cursor<T> newCursor() {
        return null;
    }

    @Override
    public Cursor<T> currentCursor() {
        return null;
    }
}
