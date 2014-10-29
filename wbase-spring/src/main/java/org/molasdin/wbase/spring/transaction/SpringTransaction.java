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

import org.molasdin.wbase.Source;
import org.molasdin.wbase.transaction.AbstractTransaction;
import org.molasdin.wbase.transaction.Engine;
import org.molasdin.wbase.transaction.EngineFactory;
import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.jdbc.JdbcEngine;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;


/**
 * Created by dbersenev on 28.10.2014.
 */
public class SpringTransaction<T extends Engine> extends AbstractTransaction<T> {
    private PlatformTransactionManager tx;
    private TransactionStatus status;
    private Source<TransactionStatus> statusSource;
    private Object savepoint;

    public SpringTransaction(EngineFactory<T> engineFactory, PlatformTransactionManager tx, Source<TransactionStatus> statusSource) {
        super(engineFactory);
        this.tx = tx;
        this.statusSource = statusSource;
    }

    protected void setSavepoint(Object savepoint){
        this.savepoint = savepoint;
    }

    @Override
    public void begin() {
        status = statusSource.value();
    }

    @Override
    public void rollback() {
        if(savepoint != null){
            try{
                status.rollbackToSavepoint(savepoint);
            } finally {
                status.releaseSavepoint(savepoint);
            }
        } else {
            tx.rollback(status);
        }
    }

    @Override
    public void commit() {
        tx.commit(status);
    }

    @Override
    public void close() {
        super.close();
        status.flush();
    }

    @Override
    public Transaction<T> nested() {
        Object tmp = status.createSavepoint();
        SpringTransaction<T> nested = new SpringTransaction<T>(engineFactory(), tx, new Source<TransactionStatus>() {
            @Override
            public TransactionStatus value() {
                return status;
            }
        });
        nested.setSavepoint(tmp);
        return nested;
    }
}
