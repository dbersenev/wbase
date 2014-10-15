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

import org.molasdin.wbase.transaction.TransactionIsolation;
import org.molasdin.wbase.transaction.TransactionRunner;
import org.molasdin.wbase.transaction.TransactionRunnerFactory;
import org.molasdin.wbase.transaction.Transactional;

/**
 * Created by dbersenev on 15.10.2014.
 */
public class BasicSupport<T> implements Support<T> {
    private TransactionRunnerFactory<T> runnerFactory;

    @Override
    public void setTransactionRunnerFactory(TransactionRunnerFactory<T> runnerFactory) {
        this.runnerFactory = runnerFactory;
    }

    @Override
    public TransactionRunnerFactory<T> transactionRunnerFactory() {
        return runnerFactory;
    }

    @Override
    public <U> U run(Transactional<T> transactional, TransactionIsolation isolation) {
        TransactionRunner<T> runner = runnerFactory.createRunner();
        runner.setIsolation(isolation);
        return runner.invoke(transactional);
    }

    @Override
    public TransactionRunner<T> runner() {
        return runnerFactory.createRunner();
    }

    @Override
    public <U> U run(Transactional<T> transactional) {
        return runnerFactory.createRunner().invoke(transactional);
    }
}
