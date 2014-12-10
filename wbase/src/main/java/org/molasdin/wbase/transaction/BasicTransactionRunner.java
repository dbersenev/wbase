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

package org.molasdin.wbase.transaction;

/**
 * Created by dbersenev on 16.10.2014.
 */
public class BasicTransactionRunner<T extends Engine> implements TransactionRunner<T> {
    private TransactionDescriptor descriptor;
    private TransactionProvider<T> transactionProvider;

    public BasicTransactionRunner() {
    }

    public BasicTransactionRunner(TransactionProvider<T> transactionProvider) {
        this.transactionProvider = transactionProvider;
    }

    @Override
    public void setTransactionProvider(TransactionProvider<T> transactionProvider) {
        this.transactionProvider = transactionProvider;
    }

    @Override
    public TransactionProvider<T> transactionProvider() {
        return transactionProvider;
    }

    @Override
    public <U> U invoke(Transactional<T, U> transactional) {
        try {
            Transaction<T> transaction = newTransaction();
            transaction.begin();
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

    protected Transaction<T> newTransaction() {
        if(descriptor != null){
            return transactionProvider.newTransaction(descriptor);
        }
        return transactionProvider.newTransaction();
    }


}
