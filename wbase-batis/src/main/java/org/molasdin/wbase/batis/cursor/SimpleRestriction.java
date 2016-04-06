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

package org.molasdin.wbase.batis.cursor;

import org.apache.commons.lang3.tuple.Pair;
import org.molasdin.wbase.storage.Order;

import java.util.List;
import java.util.Map;

/**
 * Created by dbersenev on 06.12.13.
 */
public class SimpleRestriction implements Restriction {

    private Long start;
    private Long offset;
    private List<Pair<String, Order>> orders;
    private Map<String, String> filters;

    public SimpleRestriction(Long start, List<Pair<String, Order>> orders, Map<String, String> filters) {
        this(start, null, orders, filters);
    }

    public SimpleRestriction(List<Pair<String, Order>> orders, Map<String, String> filters) {
        this(null, null, orders, filters);
    }

    public SimpleRestriction(Long start, Long size, List<Pair<String, Order>> orders, Map<String, String> filters) {
        this.orders = orders;
        this.filters = filters;
        this.start = offset;
        this.offset = start + size;
    }

    public Long getStart() {
        return start;
    }

    public Long getSize() {
        return offset;
    }

    public List<Pair<String, Order>> getOrders() {
        return orders;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

}
