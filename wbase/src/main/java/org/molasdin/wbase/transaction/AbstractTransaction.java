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
 * Created by dbersenev on 15.10.2014.
 */
public abstract class AbstractTransaction<T> implements Transaction<T> {

    private T engineContext;
    private Boolean nested;
    private EngineFactory<T> engineFactory;

    public AbstractTransaction() {
    }

    public AbstractTransaction(EngineFactory<T> engineFactory) {
        this.engineContext = engineContext;
    }

    public void setEngineFactory(EngineFactory<T> engineFactory) {
        this.engineFactory = engineFactory;
    }
    public EngineFactory<T> engineFactory(){
        return engineFactory;
    }

    @Override
    public T engine() {
        if(engineContext == null){
            engineContext = engineFactory.create();
        }
        return engineContext;
    }

    protected void setNested(Boolean nested) {
        this.nested = nested;
    }
    @Override
    public Boolean isNested() {
        return nested;
    }

    @Override
    public Transaction<T> nested() {
        return null;
    }

    @Override
    public void commitAndClose() {
        commit();
        close();
    }

    @Override
    public <U> U invokeNested(final Transactional<T, U> transactional) {
        final Transaction<T> nested = nested();
        if(nested == null){
            return null;
        }
        TransactionProvider<T> provider = new AbstractTransactionProvider<T>() {
            @Override
            public Transaction<T> newTransaction(TransactionDescriptor descriptor) {
                return nested;
            }
        };
        return new BasicTransactionRunner<T>(provider).invoke(transactional);
    }
}
