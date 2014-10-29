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
import org.molasdin.wbase.batis.support.BatisEngine;
import org.molasdin.wbase.batis.support.BatisSupport;
import org.molasdin.wbase.storage.AbstractCursor;
import org.molasdin.wbase.storage.Order;
import org.molasdin.wbase.storage.Cursor;
import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class BatisCursor<T, U> extends AbstractCursor<T> {

    private BatisSupport<U> support;
    private String mapperId;

    public BatisCursor(BatisSupport<U> support, String mapperId) {
        this.support = support;
        this.mapperId = mapperId;
    }

    @Override
    public List<T> data() {
        return postProcessData(support.run(new Transactional <BatisEngine<U>,List<T>>() {
            public List<T> run(Transaction<BatisEngine<U>> t) {
                return batisData(t.engine(), createRestriction(t.engine()));
            }
        }));
    }

    @Override
    public long totalRecords() {
        Long result = support.run(new Transactional<BatisEngine<U>, Long>() {
            public Long run(Transaction<BatisEngine<U>> t) {
                return batisCount(t.engine(), createRestriction(t.engine()));
            }
        });
        return result != null? result:0;
    }

    private Restriction createRestriction(BatisEngine<U> ctx){
        return  SimpleRestriction.create()
                .withRange(currentOffset(), pageSize())
                .withOrders(columnOrders(ctx))
                .withFilters(columnFilters(ctx));
    }

    public Cursor<T> copy() {
        return null;
    }

    public Map<String,String> columnFilters(BatisEngine<U> ctx){
        Map<String,String> newFilters = new HashMap<String, String>();
        for(String entry: filters().keySet()){
            String value = filters().get(entry).getRight();
            newFilters.put(entry, value);
        }
        return propertyToColumnFilter(ctx, newFilters);
    }

    public List<Pair<String,Order>> columnOrders(BatisEngine<U> ctx){
        List<Pair<String, Order>> oldOrder = orders();
        if(oldOrder == null){
            return null;
        }
        List<Pair<String,Order>> result = new LinkedList<Pair<String,Order>>();
        for(Pair<String, Order> entry: oldOrder){
            if(entry.getLeft() == null || entry.getRight() == null){
                return null;
            }
            result.add(Pair.of(ctx.columnByProperty(entry.getLeft(), mapperId), entry.getRight()));
        }
        return result;
    }

    private Map<String, String> propertyToColumnFilter(BatisEngine<U> ctx, Map<String,String> properties){
        Map<String,String> result = new HashMap<String, String>();
        for(String key: properties.keySet()){
            result.put(ctx.columnByProperty(key, mapperId), properties.get(key));
        }
        return result;
    }

    protected abstract List<T> batisData(BatisEngine<U> ctx, Restriction restriction);

    protected abstract Long batisCount(BatisEngine<U> ctx, Restriction restriction);

}