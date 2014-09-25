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

package org.molasdin.wbase.batis.result;

import org.apache.commons.lang3.tuple.Pair;
import org.molasdin.wbase.storage.Order;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by dbersenev on 06.12.13.
 */
public class SimpleRestriction implements Restriction {
    private int start;
    private int offset;
    private List<Pair<String, Order>> orders = new LinkedList<Pair<String, Order>>();
    private Map<String,String> filters = new HashMap<String, String>();

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public List<Pair<String, Order>> getOrders() {
        return orders;
    }

    public void setOrders(List<Pair<String, Order>> orders) {
        this.orders = orders;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
    }

    public SimpleRestriction withRange(int start, int offset){
        setStart(start);
        setOffset(offset);
        return this;
    }

    public SimpleRestriction withOrder(String property, Order propOrder){
        getOrders().add(Pair.of(property, propOrder));
        return this;
    }

    public SimpleRestriction withOrder(Pair<String, Order> order){
        getOrders().add(order);
        return this;
    }

    public SimpleRestriction withOrders(List<Pair<String, Order>> orders){
        getOrders().addAll(orders);
        return this;
    }

    public SimpleRestriction withFilters(Map<String, String> filters){
        setFilters(filters);
        return this;
    }

    public static SimpleRestriction create(){
        return new SimpleRestriction();
    }

    public static SimpleRestriction create(int start, int offset, Pair<String, Order> order, Map<String, String> filters){
        SimpleRestriction simpleRestriction = new SimpleRestriction();
        simpleRestriction.setStart(start);
        simpleRestriction.setOffset(offset);
        simpleRestriction.getOrders().add(order);
        simpleRestriction.setFilters(filters);
        return simpleRestriction;
    }
}
