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

package org.molasdin.wbase.storage.cursor;

import org.molasdin.wbase.storage.FilterAndOrder;
import org.molasdin.wbase.transaction.manager.Engine;
import org.molasdin.wbase.transaction.manager.TransactionManager;
import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.TransactionDescriptor;

import java.util.List;

/**
 * Created by dbersenev on 24.02.2016.
 */
public abstract class DelegatingExtBiDirectionalCursorFactory<T, F extends Engine> extends TransactionalExtBiDirectionalCursorFactory<T, F> {

    public DelegatingExtBiDirectionalCursorFactory(TransactionManager<F> pm, TransactionDescriptor desc) {
        super(pm, desc);
    }

    public DelegatingExtBiDirectionalCursorFactory(TransactionManager<F> pm) {
        super(pm);
    }


    @Override
    public BiDirectionalBatchCursor<T> newCursor(long pageSize) {
        long count = 0;
        try(Transaction<F> tx = processingManager().createTransaction(transactionDescriptor())){
            tx.begin();
            count = countForCursor(tx.engine(), filterAndOrder());
            tx.commit();
        }
        DelegatingBiDirectionalBatchCursor<T, F> cur =  new DelegatingBiDirectionalBatchCursor<T, F>(processingManager()) {
            @Override
            protected TransactionDescriptor descriptor() {
                return transactionDescriptor();
            }

            @Override
            public long pageSize() {
                return pageSize;
            }

            @Override
            protected List<T> loadTx(F ctx) {
                return loadForCursor(ctx, filterAndOrder());
            }
        };
        cur.setTotals(count);
        return cur;
    }

    protected abstract List<T> loadForCursor(F ctx, FilterAndOrder filterAndOrder);

    protected abstract long countForCursor(F ctx, FilterAndOrder filterAndOrder);
}
