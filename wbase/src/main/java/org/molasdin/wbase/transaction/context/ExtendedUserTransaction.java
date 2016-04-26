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

/**
 * Same as parent but introduces engine support
 * @param <T>
 */
public class ExtendedUserTransaction<T extends Engine> extends ExtendedTransaction implements UserTransaction<T> {

    protected static class UserTransactionProxy<U extends Engine> extends TransactionProxy implements UserTransaction<U>{
        private ExtendedUserTransaction<U> tx = null;

        public UserTransactionProxy(ExtendedUserTransaction<U> tx) {
            super(tx);
            this.tx = tx;
        }

        @Override
        public U engine() {
            return tx.engine();
        }

        @Override
        public void close() {
            super.close();
            this.tx = null;
        }
    }

    private T engine;

    public ExtendedUserTransaction(Transaction tx, T engine, UserTransactionContext ctx) {
        super(tx, ctx);
        this.engine = engine;
    }

    @Override
    public T engine() {
        return engine;
    }

    @SuppressWarnings("unchecked")
    public UserTransaction<T> rollbackOnlyProxy(){
        return (UserTransaction<T>)super.rollbackOnlyProxy();
    }

    @Override
    TransactionProxy prepareProxy() {
        return new UserTransactionProxy<>(this);
    }
}
