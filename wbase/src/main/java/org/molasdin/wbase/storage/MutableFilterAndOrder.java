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

package org.molasdin.wbase.storage;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by molasdin on 2/23/16.
 */
public class MutableFilterAndOrder implements FilterAndOrder {

    private List<Pair<String, Order>> orders = new LinkedList<>();

    private Map<String, Pair<FilteringMode, String>> filters = new HashMap<>();

    private List<Pair<String, Order>> ordersImm = Collections.unmodifiableList(orders);
    private Map<String, Pair<FilteringMode, String>> filtersImm = Collections.unmodifiableMap(filters);

    @Override
    public List<Pair<String, Order>> orders() {
        return ordersImm;
    }

    @Override
    public Map<String, Pair<FilteringMode, String>> filters() {
        return filtersImm;
    }

    public void addOrder(String prop, Order order) {
        orders.add(Pair.of(prop, order));
    }

    public void clearOrders() {
        orders.clear();
    }

    public void addFilter(String prop, String value, FilteringMode mode) {
        filters.put(prop, Pair.of(mode, value));
    }

    public void clearFilters() {
        filters.clear();
    }
}
