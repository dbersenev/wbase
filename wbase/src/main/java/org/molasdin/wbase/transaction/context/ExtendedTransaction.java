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

    private Map<Object, Object> resourcesArchive = new HashMap<>();
    private Set<Object> resourcesToRemove = new HashSet<>();


    private BasicTerminatableTransactionEvent terminatableEvent = null;
    private TransactionEvent commonEvent = null;

    public ExtendedTransaction(Transaction tx) {
        super(tx);
    }

    public ExtendedInterception interception(){
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
        if(!terminatableEvent.isTerminated()) {
            super.commit();
            interception.emitPostCommit(commonEvent());
        }
    }

    @Override
    public void rollback() {
        if (wasRolledBack()) {
            throw new TransactionRolledBackException();
        }
        interception.emitPreRollback(terminatableEvent());
        if(!terminatableEvent.isTerminated()) {
            super.rollback();
            interception.emitPostRollback(commonEvent());
        }
    }

    @Override
    public void close() {
        if (!(wasCommitted() || wasRolledBack())) {
            this.rollback();
        }
        interception.emitPreClose(commonEvent());
        super.close();
        resourcesArchive = null;
        interception.emitPostClose(commonEvent());
    }

    public Map<Object, Object> resourcesArchive() {
        return resourcesArchive;
    }

    public Set<Object> resourcesToRemove() {
        return resourcesToRemove;
    }

    private TransactionEvent commonEvent(){
        if (commonEvent == null) {
            commonEvent = () -> this;
        }

        return commonEvent;
    }

    private TerminatableTransactionEvent terminatableEvent(){
        if (terminatableEvent == null) {
            terminatableEvent = new BasicTerminatableTransactionEvent(this);
        } else {
            terminatableEvent.resetTerminated();
        }
        return terminatableEvent;
    }

}
