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

import org.molasdin.wbase.transaction.manager.Engine;
import org.molasdin.wbase.transaction.TransactionDescriptor;
import org.molasdin.wbase.transaction.manager.TransactionManager;

/**
 * Created by dbersenev on 15.10.2014.
 */
public interface TransactionRunner<T extends Engine> {
    void setTransactionManager(TransactionManager<T> transactionManager);
    TransactionManager<T> transactionProvider();

    <U> U call(Transactional<T, U> transactional);
    void execute(TransactionalVoid<T> transactional);

    void setDescriptor(TransactionDescriptor descriptor);
}
