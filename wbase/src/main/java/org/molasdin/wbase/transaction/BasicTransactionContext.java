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
public class BasicTransactionContext<T> implements TransactionContext<T> {

    private T engineContext;

    public BasicTransactionContext() {
    }

    public BasicTransactionContext(T engineContext) {
        this.engineContext = engineContext;
    }

    @Override
    public void rollback() {

    }

    @Override
    public T engineContext() {
        return engineContext;
    }

    @Override
    public void runInner(Transactional<TransactionContext<T>> transactional) {

    }
}
