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

package org.molasdin.wbase.storage;

import org.molasdin.wbase.transaction.*;

/**
 * Created by dbersenev on 15.10.2014.
 */
public class BasicSupport<T extends Engine> implements Support<T> {
    private TransactionProviderFactory<T> providerFactory;

    @Override
    public void setTransactionProviderFactory(TransactionProviderFactory<T> runnerFactory) {
        this.providerFactory = runnerFactory;
    }

    @Override
    public TransactionProviderFactory<T> transactionProviderFactory() {
        if(providerFactory != null){
            return providerFactory;
        }
        synchronized (this){
            if(providerFactory != null){
                return providerFactory;
            }
            setTransactionProviderFactory(newDefaultFactory());
        }
        return providerFactory;
    }

    @Override
    public <U> U run(Transactional<T, U> transactional, TransactionIsolation isolation) {
        return newRunner(isolation).invoke(transactional);
    }

    @Override
    public TransactionProvider<T> newTransactionProvider() {
        return providerFactory.createProvider();
    }

    @Override
    public Transaction<T> newTransaction() {
        return newTransaction(null);
    }

    @Override
    public Transaction<T> newTransaction(TransactionIsolation isolation) {
        TransactionProvider<T> provider = newTransactionProvider();
        if (isolation != null) {
            return provider.newTransaction(isolation);
        }
        return provider.newTransaction();
    }

    @Override
    public TransactionRunner<T> newRunner() {
        return newRunner(null);
    }

    @Override
    public TransactionRunner<T> newRunner(TransactionIsolation isolation) {
        TransactionProvider<T> provider = newTransactionProvider();
        TransactionRunner<T> runner = new BasicTransactionRunner<T>(provider);
        if (isolation != null) {
            runner.setIsolation(isolation);
        }
        return runner;
    }

    public TransactionProviderFactory<T> newDefaultFactory(){
        return null;
    }

    @Override
    public <U> U run(Transactional<T, U> transactional) {
        return newRunner().invoke(transactional);
    }
}
