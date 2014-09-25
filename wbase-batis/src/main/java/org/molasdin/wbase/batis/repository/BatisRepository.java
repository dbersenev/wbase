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
import org.molasdin.wbase.batis.result.BatisCursor;
import org.molasdin.wbase.batis.result.Restriction;
import org.molasdin.wbase.batis.search.Search;
import org.molasdin.wbase.batis.support.BatisContext;
import org.molasdin.wbase.batis.support.BatisSupport;
import org.molasdin.wbase.storage.*;
import org.molasdin.wbase.storage.spec.MapSearchSpecification;
import org.molasdin.wbase.transaction.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by dbersenev on 13.03.14.
 */
public class BatisRepository<T extends Storable<T>, M extends CommonMapper<T>> implements Repository<T> {
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
    public T byId(final Serializable id) {
        return support.run(new Transactional<BatisContext<M>, T>() {
            @Override
            public T run(BatisContext<M> context) throws Exception {
                return context.mapper().findById(id);
            }
        });
    }

    public Cursor<T> queryBySpec(MapSearchSpecification<T> spec) {
        return queryByMap(spec.parameters());
    }

    public Cursor<T> queryByMap(Map<String, Object> parameters) {
        final SearchConfiguration<T, Map<String, Object>> spec = Search.fromMap(parameters);
        return new BatisCursor<T, M>(support, mapperId) {
            @Override
            protected List<T> batisData(BatisContext<M> ctx, Restriction restriction) {
                return ctx.mapper().bySpec(spec.query(), restriction);
            }

            @Override
            protected Long batisCount(BatisContext<M> ctx, Restriction restriction) {
                return ctx.mapper().bySpecCount(spec.query(), restriction);
            }
        };

    }

    @Override
    public <U> Cursor<U> filteredCollection(T owner, Collection<U> collection) {
        return null;
    }

    @Override
    public <U> List<U> simpleFilteredCollection(T owner, Collection<U> collection, String filter) {
        return null;
    }

    @Override
    public List<T> byQuery(String query, Map<String, ?> arguments) {
        return null;
    }

    @Override
    public List<T> allAtOnce(String orderProp, Order order) {
        return null;
    }

    @Override
    public void save(final T o) {
        support.run(new Transactional<BatisContext<M>, Void>() {
            @Override
            public Void run(BatisContext<M> context) throws Exception {
                context.mapper().save(o);
                return null;
            }
        });
    }

    @Override
    public void saveOrUpdate(final T o) {
        support.run(new Transactional<BatisContext<M>, Void>() {
            @Override
            public Void run(BatisContext<M> context) throws Exception {
                if (o.id() != null) {
                    context.mapper().update(o);
                } else {
                    context.mapper().save(o);
                }
                return null;
            }
        });
    }

    @Override
    public void merge(T o) {
    }

    @Override
    public void update(final T o) {
        support.run(new Transactional<BatisContext<M>, Void>() {
            @Override
            public Void run(BatisContext<M> context) throws Exception {
                context.mapper().update(o);
                return null;
            }
        });
    }

    @Override
    public void remove(final T o) {
        support.run(new Transactional<BatisContext<M>, Void>() {
            @Override
            public Void run(BatisContext<M> context) throws Exception {
                context.mapper().remove(o.id());
                return null;
            }
        });
    }

    @Override
    public void refresh(T o) {

    }

    @Override
    public void refreshChild(T o, Object child) {

    }


}
