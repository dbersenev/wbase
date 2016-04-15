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

package org.molasdin.wbase.transaction.context;

import org.molasdin.wbase.transaction.AbstractUserTransaction;
import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.UserTransaction;
import org.molasdin.wbase.transaction.manager.Engine;

/**
 * Created by molasdin on 3/29/16.
 */
public class ExtendedUserTransaction<T extends Engine> extends ExtendedTransaction implements UserTransaction<T> {

    private static class TransactionProxy<U extends Engine> implements UserTransaction<U> {
        private ExtendedUserTransaction<U> tx = null;

        public TransactionProxy(ExtendedUserTransaction<U> tx) {
            this.tx = tx;
        }

        @Override
        public UserTransactionContext context() {
            return tx.context();
        }

        @Override
        public void rollback() {
            tx.rollback();
        }

        @Override
        public boolean wasRolledBack() {
            return tx.wasRolledBack();
        }

        @Override
        public U engine() {
            return tx.engine();
        }

        @Override
        public void close() {
            tx = null;
        }
    }

    private T engine;

    private UserTransaction<T> rollbackOnlyProxy = null;

    public ExtendedUserTransaction(Transaction tx, T engine, UserTransactionContext ctx) {
        super(tx, ctx);
        this.engine = engine;
    }

    @Override
    public T engine() {
        return engine;
    }

    @Override
    public void close() {
        super.close();
        if(rollbackOnlyProxy != null){
            rollbackOnlyProxy.close();
            rollbackOnlyProxy = null;
        }
    }

    public UserTransaction<T> rollbackOnlyProxy(){
        if(!isClosed() && rollbackOnlyProxy == null){
            rollbackOnlyProxy =  new TransactionProxy<>(this);
        }
        return rollbackOnlyProxy;
    }
}
