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

package org.molasdin.wbase.transaction.context;

import org.molasdin.wbase.transaction.DelegatingTransaction;
import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.context.interceptors.*;
import org.molasdin.wbase.transaction.exceptions.TransactionCommittedException;
import org.molasdin.wbase.transaction.exceptions.TransactionRolledBackException;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by molasdin on 3/19/16.
 */
public class ExtendedTransaction extends DelegatingTransaction {

    private ExtendedInterception interception = new ExtendedInterception();

    private UserTransactionContext ctx;
    private BasicTerminatableTransactionEvent terminatableEvent = null;
    private TransactionEvent commonEvent = null;

    private Transaction rollbackOnlyProxy = null;
    private boolean closed = false;

    private static class TransactionProxy implements Transaction {
        private ExtendedTransaction tx = null;

        public TransactionProxy(ExtendedTransaction tx) {
            this.tx = tx;
        }

        @Override
        public UserTransactionContext context() {
            return tx.context();
        }

        @Override
        public void rollback() {
            tx.rollback();
        }

        @Override
        public boolean wasRolledBack() {
            return tx.wasRolledBack();
        }

        @Override
        public void close() {
            this.tx = null;
        }
    }

    public ExtendedTransaction(Transaction tx, UserTransactionContext ctx) {
        super(tx);
        this.ctx = ctx;
    }

    public boolean isClosed(){
        return closed;
    }

    public ExtendedInterception interception() {
        return interception;
    }

    public void begin() {
        interception.emitStart(commonEvent());
    }

    @Override
    public void commit() {
        if (wasCommitted()) {
            throw new TransactionCommittedException();
        }
        interception.emitPreCommit(terminatableEvent());
        if (!terminatableEvent.isTerminated()) {
            super.commit();
            interception.emitPostCommit(commonEvent());
        }
    }

    @Override
    public UserTransactionContext context() {
        return ctx;
    }

    @Override
    public void rollback() {
        if (wasRolledBack()) {
            throw new TransactionRolledBackException();
        }
        interception.emitPreRollback(terminatableEvent());
        if (!terminatableEvent.isTerminated()) {
            super.rollback();
            interception.emitPostRollback(commonEvent());
        }
    }

    @Override
    public void close() {
        if(!isClosed()){
            if (!(wasCommitted() || wasRolledBack())) {
                this.rollback();
            }
            interception.emitPreClose(commonEvent());
            super.close();
            interception.emitPostClose(commonEvent());
            if (rollbackOnlyProxy != null) {
                rollbackOnlyProxy.close();
                rollbackOnlyProxy = null;
            }
            closed = true;
            interception = null;
            ctx = null;
        }
    }

    public Transaction rollbackOnlyProxy() {
        if (!closed && rollbackOnlyProxy == null) {
            rollbackOnlyProxy = new TransactionProxy(this);
        }
        return rollbackOnlyProxy;
    }

    private TransactionEvent commonEvent() {
        if (commonEvent == null) {
            commonEvent = () -> this;
        }

        return commonEvent;
    }

    private TerminatableTransactionEvent terminatableEvent() {
        if (terminatableEvent == null) {
            terminatableEvent = new BasicTerminatableTransactionEvent(this);
        } else {
            terminatableEvent.resetTerminated();
        }
        return terminatableEvent;
    }

}
