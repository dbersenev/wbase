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

package org.molasdin.wbase.batis.repository;

import org.molasdin.wbase.batis.CommonMapper;
import org.molasdin.wbase.batis.cursor.BatisCursor;
import org.molasdin.wbase.batis.cursor.Restriction;
import org.molasdin.wbase.batis.search.Search;
import org.molasdin.wbase.batis.support.BatisEngine;
import org.molasdin.wbase.batis.support.BatisSupport;
import org.molasdin.wbase.storage.*;
import org.molasdin.wbase.storage.spec.MapSearchSpecification;
import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.TransactionDescriptor;
import org.molasdin.wbase.transaction.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by dbersenev on 13.03.14.
 */
public class BatisRepository<T, M extends CommonMapper<T>, K extends Serializable> implements Repository<T, K> {
    private String mapperId;

    private BatisSupport<M> support;

    public BatisSupport<M> support() {
        return support;
    }

    public BatisRepository(BatisSupport<M> support) {
        this.support = support;
    }

    public void setMapperId(String mapperId) {
        this.mapperId = mapperId;
    }

    @Override
    public Cursor<T> all() {
        return queryByMap(Collections.<String, Object>emptyMap());
    }

    @Override
    public T byId(final K id) {
        return support.run(new Transactional<BatisEngine<M>, T>() {
            @Override
            public T run(Transaction<BatisEngine<M>> t) throws Exception {
                return t.engine().mapper().findById(id);
            }
        });
    }

    public Cursor<T> queryBySpec(MapSearchSpecification<T> spec) {
        return queryByMap(spec.parameters());
    }

    protected TransactionDescriptor defaultCursorTransactionCfg(){
        return null;
    }

    public Cursor<T> queryByMap(Map<String, Object> parameters) {
        final SearchConfiguration<T, Map<String, Object>> spec = Search.fromMap(parameters);
        BatisCursor<T, M> cur =  new BatisCursor<T, M>(support, mapperId) {
            @Override
            protected List<T> batisData(BatisEngine<M> ctx, Restriction restriction) {
                return ctx.mapper().bySpec(spec.query(), restriction);
            }

            @Override
            protected Long batisCount(BatisEngine<M> ctx, Restriction restriction) {
                return ctx.mapper().bySpecCount(spec.query(), restriction);
            }
        };
        cur.setTransactionDescriptor(defaultCursorTransactionCfg());
        return cur;
    }

    @Override
    public List<T> allAtOnce(String orderProp, Order order) {
        return null;
    }

    @Override
    public void save(final T o) {
        support.run(new Transactional<BatisEngine<M>, Void>() {
            @Override
            public Void run(Transaction<BatisEngine<M>> t) throws Exception {
                t.engine().mapper().save(o);
                return null;
            }
        });
    }

    @Override
    public void saveOrUpdate(final T o) {
        support.run(new Transactional<BatisEngine<M>, Void>() {
            @Override
            public Void run(Transaction<BatisEngine<M>> t) throws Exception {
                    t.engine().mapper().saveOrUpdate(o);
                return null;
            }
        });
    }

    @Override
    public void merge(T o) {
    }

    @Override
    public void update(final T o) {
        support.run(new Transactional<BatisEngine<M>, Void>() {
            @Override
            public Void run(Transaction<BatisEngine<M>> t) throws Exception {
                t.engine().mapper().update(o);
                return null;
            }
        });
    }

    @Override
    public void remove(final T o) {
        support.run(new Transactional<BatisEngine<M>, Void>() {
            @Override
            public Void run(Transaction<BatisEngine<M>> t) throws Exception {
                t.engine().mapper().remove(o);
                return null;
            }
        });
    }
}
