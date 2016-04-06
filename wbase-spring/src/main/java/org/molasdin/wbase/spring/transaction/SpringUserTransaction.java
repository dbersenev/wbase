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

package org.molasdin.wbase.spring.transaction;

import org.molasdin.wbase.transaction.AbstractUserTransaction;
import org.molasdin.wbase.transaction.manager.Engine;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;


/**
 * Created by dbersenev on 28.10.2014.
 */
public class SpringUserTransaction<T extends Engine> extends AbstractUserTransaction<T> {
    private PlatformTransactionManager tx;
    private TransactionStatus status;
    private Object savepoint;

    public SpringUserTransaction(T engine, PlatformTransactionManager tx, TransactionStatus status) {
        super(engine);
        this.tx = tx;
        this.status = status;
    }

    protected void setSavepoint(Object savepoint){
        this.savepoint = savepoint;
    }

    @Override
    public void rollback() {
        if(savepoint != null){
            status.rollbackToSavepoint(savepoint);
        } else {
            tx.rollback(status);
        }
        super.rollback();
    }

    @Override
    public void commit() {
        tx.commit(status);
        super.commit();
    }

    @Override
    public void close() {
        if(savepoint != null){
            status.releaseSavepoint(savepoint);
        }
        status.flush();
    }
}
