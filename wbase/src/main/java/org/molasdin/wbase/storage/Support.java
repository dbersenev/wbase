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
import org.molasdin.wbase.transaction.manager.Engine;
import org.molasdin.wbase.transaction.manager.TransactionManager;
import org.molasdin.wbase.transaction.runner.BasicTransactionRunner;
import org.molasdin.wbase.transaction.runner.TransactionRunner;
import org.molasdin.wbase.transaction.runner.Transactional;
import org.molasdin.wbase.transaction.runner.TransactionalVoid;

/**
 * Created by dbersenev on 15.10.2014.
 */
public interface Support<T extends Engine> {
    void setDefaultTransactionProvider(TransactionManager<T> provider);
    TransactionManager<T> defaultTransactionProvider();

    void setDefaultDescriptor(TransactionDescriptor descriptor);
    TransactionDescriptor defaultDescriptor();

    default TransactionRunner<T> newRunner() {
        return newRunner(defaultTransactionProvider(), defaultDescriptor());
    }
    default TransactionRunner<T> newRunner(TransactionDescriptor descriptor){
        TransactionManager<T> provider = defaultTransactionProvider();
        TransactionRunner<T> runner = new BasicTransactionRunner<T>(provider);
        if (descriptor != null) {
            runner.setDescriptor(descriptor);
        }
        return runner;
    }
    default TransactionRunner<T> newRunnerWithIsolation(TransactionIsolation isolation) {
        return newRunner(defaultTransactionProvider(), TransactionDescriptors.INSTANCE.isolated(isolation));
    }
    default TransactionRunner<T> newRunner(TransactionManager<T> provider) {
        return newRunner(provider, defaultDescriptor());
    }
    default TransactionRunner<T> newRunnerWithIsolation(TransactionManager<T> provider, TransactionIsolation isolation) {
        return newRunner(provider, TransactionDescriptors.INSTANCE.isolated(isolation));
    }
    default TransactionRunner<T> newRunner(TransactionManager<T> provider, TransactionDescriptor isolation) {
        TransactionRunner<T> r = new BasicTransactionRunner<T>(provider);
        if(isolation != null){
            r.setDescriptor(isolation);
        }
        return r;
    }


    default  <U> U runWithIsolation(Transactional<T, U> transactional, TransactionIsolation isolation) {
        return newRunnerWithIsolation(isolation).call(transactional);
    }
    default  <U> U run(Transactional<T, U> transactional, TransactionDescriptor descriptor){
        return newRunner(descriptor).call(transactional);
    }
    default  <U> U run(Transactional<T, U> transactional) {
        return newRunner().call(transactional);
    }
    default <U> U run(TransactionManager<T> provider, Transactional<T, U> transactional) {
        return newRunner(provider).call(transactional);
    }
    default  <U> U runWithIsolation(TransactionManager<T> provider, TransactionIsolation isolation, Transactional<T, U> transactional) {
        return newRunnerWithIsolation(provider, isolation).call(transactional);
    }
    default  <U> U run(TransactionManager<T> provider, Transactional<T, U> transactional, TransactionDescriptor descriptor) {
        return newRunner(provider, descriptor).call(transactional);
    }


    default UserTransaction<T> newTransaction() {
        return newTransaction(defaultTransactionProvider(), defaultDescriptor());
    }
    default UserTransaction<T> newTransactionWithIsolation(TransactionIsolation isolation) {
        return newTransaction(defaultTransactionProvider(), TransactionDescriptors.INSTANCE.isolated(isolation));
    }
    default UserTransaction<T> newTransaction(TransactionDescriptor descriptor){
        TransactionManager<T> provider = defaultTransactionProvider();
        if (descriptor != null) {
            return provider.createTransaction(descriptor);
        }
        return provider.createTransaction();
    }
    default UserTransaction<T> newTransaction(TransactionManager<T> provider) {
        return newTransaction(provider, defaultDescriptor());
    }
    default UserTransaction<T> newTransactionWithIsolation(TransactionManager<T> provider, TransactionIsolation isolation) {
        return newTransaction(provider, TransactionDescriptors.INSTANCE.isolated(isolation));
    }
    default UserTransaction<T> newTransaction(TransactionManager<T> provider, TransactionDescriptor descriptor) {
        if(descriptor != null){
            return provider.createTransaction(descriptor);
        }
        return provider.createTransaction();
    }


    default void execute(TransactionalVoid<T> transactional) {
        newRunner().execute(transactional);
    }
    default void executeWithIsolation(TransactionalVoid<T> transactional, TransactionIsolation isolation) {
        newRunnerWithIsolation(isolation).execute(transactional);
    }
    default void execute(TransactionalVoid<T> transactional, TransactionDescriptor descriptor) {
        newRunner(descriptor).execute(transactional);
    }
}
