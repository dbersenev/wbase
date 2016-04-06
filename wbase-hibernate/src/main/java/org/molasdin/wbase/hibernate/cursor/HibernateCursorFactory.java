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

package org.molasdin.wbase.hibernate.cursor;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.molasdin.wbase.hibernate.HibernateEngine;
import org.molasdin.wbase.hibernate.criterion.FilterCriterion;
import org.molasdin.wbase.hibernate.search.CursorCriteria;
import org.molasdin.wbase.hibernate.util.HibernateUtils;
import org.molasdin.wbase.storage.FilteringMode;
import org.molasdin.wbase.storage.Order;
import org.molasdin.wbase.storage.cursor.*;
import org.molasdin.wbase.transaction.UserTransaction;
import org.molasdin.wbase.transaction.manager.TransactionManager;
import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.TransactionDescriptor;

import java.util.List;
import java.util.Map;

/**
 * Created by dbersenev on 03.03.2016.
 */
public class HibernateCursorFactory<T> extends TransactionalExtBiDirectionalCursorFactory<T, HibernateEngine> {

    private CursorCriteria cursorCriteria;

    public HibernateCursorFactory(TransactionManager<HibernateEngine> pm, CursorCriteria cursorCriteria) {
        super(pm);
        this.cursorCriteria = cursorCriteria;
    }

    public HibernateCursorFactory(TransactionManager<HibernateEngine> pm, TransactionDescriptor desc, CursorCriteria cursorCriteria) {
        super(pm, desc);
        this.cursorCriteria = cursorCriteria;
    }

    @Override
    public BiDirectionalBatchCursor<T> newCursor(long pageSize) {
        long count = 0;
        try (UserTransaction<HibernateEngine> tx = processingManager().createTransaction(transactionDescriptor())) {
            DetachedCriteria countCriteria = cursorCriteria.newCountCriteria();
            countCriteria.add(populateFilters());
            Long result = (Long) countCriteria.getExecutableCriteria(tx.engine().session()).uniqueResult();
            count = result != null ? result : 0;
            tx.commit();
        }
        if(count == 0) {
            return EmptyCursor.emptyTwoWaysBatchCursor();
        }
        DetachedCriteria criteria = applyOrdersAndFilters();
        return new BasicHibernateCursor<T>(processingManager(), criteria, count) {
            @Override
            protected TransactionDescriptor descriptor() {
                return HibernateCursorFactory.this.transactionDescriptor();
            }

            @Override
            public long pageSize() {
                return pageSize;
            }
        };
    }

    private DetachedCriteria applyOrdersAndFilters() {
        DetachedCriteria search = cursorCriteria.newSearchCriteria();

        List<Pair<String, Order>> orders = filterAndOrder().orders();
        if (!orders.isEmpty()) {
            for (Pair<String, org.molasdin.wbase.storage.Order> order : orders) {
                String prop = order.getLeft();
                search.addOrder(org.molasdin.wbase.storage.Order.ASC.equals(order.getRight()) ? org.hibernate.criterion.Order.asc(prop) :
                        org.hibernate.criterion.Order.desc(prop));
            }
        }
        search.add(populateFilters());
        return search;
    }

    private Conjunction populateFilters() {
        Conjunction filterCriterion = Restrictions.conjunction();
        Map<String, Pair<FilteringMode, String>> f = filterAndOrder().filters();
        if (f.size() > 0) {
            for (String prop : f.keySet()) {
                Pair<FilteringMode, String> entry = f.get(prop);
                MatchMode m = HibernateUtils.toMatchMode(entry.getLeft());
                filterCriterion.add(new FilterCriterion(prop, entry.getRight(), m));
            }
        }
        return filterCriterion;
    }
}
