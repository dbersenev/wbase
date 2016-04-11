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

    private T engine;

    private UserTransaction<T> rollbackOnlyProxy = null;

    public ExtendedUserTransaction(Transaction tx, T engine, UserTransactionContext ctx) {
        super(tx, ctx);
        this.engine = engine;
    }

    public UserTransaction<T> rollbackOnlyProxy(){
        if(rollbackOnlyProxy == null){
            rollbackOnlyProxy =  new AbstractUserTransaction<T>(engine) {
                @Override
                public UserTransactionContext context() {
                    return ExtendedUserTransaction.this.context();
                }

                @Override
                public void rollback() {
                    ExtendedUserTransaction.this.rollback();
                }

                @Override
                public boolean wasRolledBack() {
                    return ExtendedUserTransaction.this.wasRolledBack();
                }
            };
        }
        return rollbackOnlyProxy;
    }
}
