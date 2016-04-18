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

package org.molasdin.wbase.hibernate.cursor;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Query;
import org.hibernate.Session;
import org.molasdin.wbase.hibernate.HibernateEngine;
import org.molasdin.wbase.hibernate.HibernateMatchMode;
import org.molasdin.wbase.storage.FilteringMode;
import org.molasdin.wbase.storage.Order;
import org.molasdin.wbase.storage.cursor.DelegatingBiDirectionalBatchCursor;
import org.molasdin.wbase.transaction.manager.TransactionManager;

import java.util.List;
import java.util.Map;

public class BasicHibernateQueryCursor<T> extends DelegatingBiDirectionalBatchCursor<T, HibernateEngine> {

    private String query;

    public BasicHibernateQueryCursor(TransactionManager<HibernateEngine> pm, String query, long total) {
        super(pm);
        setSize(total);
        this.query = query;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<T> loadTx(HibernateEngine ctx) {
        Query q = ctx.session().createQuery(query);
        configure(q);
        return (List<T>)q.setFirstResult((int)currentOffset())
                .setMaxResults((int)pageSize()).list();
    }

    protected void configure(Query q){
    }
}
