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

package org.molasdin.wbase.transaction.runner;

import org.molasdin.wbase.transaction.UserTransaction;
import org.molasdin.wbase.transaction.*;
import org.molasdin.wbase.transaction.exceptions.TransactionInterruptedException;
import org.molasdin.wbase.transaction.manager.Engine;
import org.molasdin.wbase.transaction.manager.TransactionManager;

/**
 * Created by dbersenev on 16.10.2014.
 */
public class BasicTransactionRunner<T extends Engine> implements TransactionRunner<T> {
    private TransactionDescriptor descriptor;
    private TransactionManager<T> transactionManager;

    public BasicTransactionRunner() {
    }

    public BasicTransactionRunner(TransactionManager<T> transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setTransactionManager(TransactionManager<T> transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public TransactionManager<T> transactionProvider() {
        return transactionManager;
    }

    @Override
    public void execute(TransactionalVoid<T> transactional) {
        call((tx) -> {transactional.run(tx); return null;});
    }

    @Override
    public <U> U call(Transactional<T, U> transactional) {
        try {
            UserTransaction<T> transaction = newTransaction();
            try {
                U result = transactional.run(transaction);
                transaction.commit();
                return result;
            } catch (TransactionInterruptedException ex) {
                return null;
            } catch (Exception ex) {
                transaction.rollback();
                throw ex;
            } finally {
                transaction.close();
            }
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void setDescriptor(TransactionDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public TransactionDescriptor descriptor() {
        return descriptor;
    }

    protected UserTransaction<T> newTransaction() {
        if(descriptor != null){
            return transactionManager.createTransaction(descriptor);
        }
        return transactionManager.createTransaction();
    }


}
