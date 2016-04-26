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

import org.molasdin.wbase.transaction.AbstractTransaction;
import org.molasdin.wbase.transaction.DelegatingTransaction;
import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.context.interceptors.*;
import org.molasdin.wbase.transaction.exceptions.TransactionCommittedException;
import org.molasdin.wbase.transaction.exceptions.TransactionInactiveException;
import org.molasdin.wbase.transaction.exceptions.TransactionRolledBackException;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by molasdin on 3/19/16.
 */

/**
 * Wrapper for transactions which introduces events and better control over transaction
 */
public class ExtendedTransaction extends DelegatingTransaction {

    private ExtendedInterception interception = new ExtendedInterception();

    private UserTransactionContext ctx;
    private BasicTerminatableTransactionEvent terminatableEvent = null;
    private TransactionEvent commonEvent = null;

    private TransactionProxy rollbackOnlyProxy = null;
    private boolean closed = false;

    /**
     * Class used to represent proxy for inner transaction of the same manager
     */
    protected static class TransactionProxy extends AbstractTransaction {
        private ExtendedTransaction tx = null;

        public TransactionProxy(ExtendedTransaction tx) {
            this.tx = tx;
        }

        @Override
        public UserTransactionContext context() {
            throwIfInactive();
            return tx.context();
        }

        @Override
        public void rollback() {
            throwIfInactive();
            super.rollback();
            tx.rollback();
        }

        @Override
        public void commit() {
            throwIfInactive();
            super.commit();
        }

        @Override
        public boolean wasRolledBack() {
            return tx.wasRolledBack();
        }

        @Override
        public void close() {
            if (tx == null) {
                return;
            }
            if (!wasCommitted() && !wasRolledBack()) {
                rollback();
            }
            this.tx = null;
        }

        /**
         * Allows to reuse same instance
         * @param outer
         */
        void restore(ExtendedTransaction outer) {
            if (outer.wasRolledBack()) {
                throw new TransactionRolledBackException();
            }
            this.tx = outer;
            setCommitted(false);
        }

        boolean isInactive() {
            return tx == null;
        }

        private void throwIfInactive() {
            if (tx == null) {
                throw new TransactionInactiveException();
            }
        }
    }

    public ExtendedTransaction(Transaction tx, UserTransactionContext ctx) {
        super(tx);
        this.ctx = ctx;
    }

    public boolean isClosed() {
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
        checkStatus();
        interception.emitPreCommit(terminatableEvent());
        if (!terminatableEvent.isTerminated()) {
            super.commit();
            interception.emitPostCommit(commonEvent());
        }
        removeProxy();
    }

    @Override
    public UserTransactionContext context() {
        return ctx;
    }

    @Override
    public void rollback() {
        checkStatus();
        interception.emitPreRollback(terminatableEvent());
        if (!terminatableEvent.isTerminated()) {
            super.rollback();
            interception.emitPostRollback(commonEvent());
        }
        removeProxy();
    }

    @Override
    public void close() {
        if (!isClosed()) {
            closed = true;
            if (!(wasCommitted() || wasRolledBack())) {
                this.rollback();
            }
            interception.emitPreClose(commonEvent());
            super.close();
            interception.emitPostClose(commonEvent());
            removeProxy();
            interception = null;
            ctx = null;
        }
    }

    public Transaction rollbackOnlyProxy() {
        checkStatus();
        if (!closed && rollbackOnlyProxy == null) {
            rollbackOnlyProxy = prepareProxy();
        } else if (rollbackOnlyProxy != null && rollbackOnlyProxy.isInactive()) {
            rollbackOnlyProxy.restore(this);
        }
        return rollbackOnlyProxy;
    }

    TransactionProxy prepareProxy() {
        return new TransactionProxy(this);
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

    private void checkStatus() {
        if (wasRolledBack()) {
            throw new TransactionRolledBackException();
        }
        if (wasCommitted()) {
            throw new TransactionCommittedException();
        }
    }

    private void removeProxy(){
        if(rollbackOnlyProxy != null) {
            rollbackOnlyProxy.close();
        }
        rollbackOnlyProxy = null;
    }

}
