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

package org.molasdin.wbase.transaction.context.interceptors;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by molasdin on 4/6/16.
 */
public class ExtendedInterception implements Interception{

    private ExtendedInterception parent = null;
    private Set<ExtendedInterception> interceptions = new LinkedHashSet<>();

    private Set<Interceptor<TransactionEvent>> startListeners = new LinkedHashSet<>();

    private Set<Interceptor<TerminatableTransactionEvent>> preCommitListeners = new LinkedHashSet<>();
    private Set<Interceptor<TransactionEvent>> postCommitListeners = new LinkedHashSet<>();

    private Set<Interceptor<TransactionEvent>> preCloseListeners = new LinkedHashSet<>();
    private Set<Interceptor<TransactionEvent>> postCloseListeners = new LinkedHashSet<>();

    private Set<Interceptor<TerminatableTransactionEvent>> preRollbackListeners = new LinkedHashSet<>();
    private Set<Interceptor<TransactionEvent>> postRollbackListeners = new LinkedHashSet<>();

    private Map<Interceptor<? extends TransactionEvent>, InterceptionTrigger> lookup = new HashMap<>();

    @Override
    public <U extends TransactionEvent> void remove(Interceptor<U> consumer) {
        if(lookup.containsKey(consumer)) {
            InterceptionTrigger tr = lookup.get(consumer);
            switch (tr) {
                case START:
                    startListeners.remove(consumer);
                    break;
                case PRE_COMMIT:
                    preCommitListeners.remove(consumer);
                    break;
                case POST_COMMIT:
                    postCommitListeners.remove(consumer);
                    break;
                case PRE_ROLLBACK:
                    preRollbackListeners.remove(consumer);
                    break;
                case POST_ROLLBACK:
                    postRollbackListeners.remove(consumer);
                    break;
                case PRE_CLOSE:
                    preCloseListeners.remove(consumer);
                    break;
                case POST_CLOSE:
                    postCloseListeners.remove(consumer);
            }
            lookup.remove(consumer);
        }
    }

    public void addInterception(ExtendedInterception interception){
        interceptions.add(interception);
        interception.parent = this;
    }

    public void addStart(Interceptor<TransactionEvent> consumer) {
        startListeners.add(consumer);
    }
    public void emitStart(TransactionEvent event){
        invokeCommonListeners(event, startListeners);
        for(ExtendedInterception entry: interceptions) {
            entry.emitStart(event);
        }
    }

    public void addPreCommit(Interceptor<TerminatableTransactionEvent> consumer) {
        preCommitListeners.add(consumer);
    }
    public void emitPreCommit(TerminatableTransactionEvent event){
        invokeTerminatableListeners(event, preCommitListeners);
        for(ExtendedInterception entry: interceptions) {
            entry.emitPreCommit(event);
        }
    }

    public void addPostCommit(Interceptor<TransactionEvent> consumer) {
        postCommitListeners.add(consumer);
    }
    public void emitPostCommit(TransactionEvent event){
        invokeCommonListeners(event,  postCommitListeners);
        for(ExtendedInterception entry: interceptions) {
            entry.emitPostCommit(event);
        }
    }

    public void addPreRollback(Interceptor<TerminatableTransactionEvent> consumer) {
        preRollbackListeners.add(consumer);
    }
    public void emitPreRollback(TerminatableTransactionEvent event){
        invokeTerminatableListeners(event, preRollbackListeners);
        for(ExtendedInterception entry: interceptions) {
            entry.emitPreRollback(event);
        }
    }

    public void addPostRollback(Interceptor<TransactionEvent> consumer) {
        postRollbackListeners.add(consumer);
    }
    public void emitPostRollback(TransactionEvent event){
        invokeCommonListeners(event, postRollbackListeners);
        for(ExtendedInterception entry: interceptions) {
            entry.emitPostRollback(event);
        }
    }

    public void addPreClose(Interceptor<TransactionEvent> consumer) {
        preCloseListeners.add(consumer);
    }
    public void emitPreClose(TransactionEvent event){
        invokeCommonListeners(event, preCloseListeners);
        for(ExtendedInterception entry: interceptions) {
            entry.emitPreClose(event);
        }
    }

    public void addPostClose(Interceptor<TransactionEvent> consumer) {
        postCloseListeners.add(consumer);
    }
    public void emitPostClose(TransactionEvent event){
        invokeCommonListeners(event, postCloseListeners);
        for(ExtendedInterception entry: interceptions) {
            entry.emitPostClose(event);
        }
    }

    public void detach(){
        parent.interceptions.remove(this);
        parent = null;
    }

    private void invokeCommonListeners(TransactionEvent event, Set<Interceptor<TransactionEvent>> interceptors) {
        for (Interceptor<TransactionEvent> entry : interceptors) {
            entry.intercept(event);
        }
    }

    private void invokeTerminatableListeners(TerminatableTransactionEvent event, Set<Interceptor<TerminatableTransactionEvent>> interceptors) {
        for (Interceptor<TerminatableTransactionEvent> entry : interceptors) {
            entry.intercept(event);
        }
    }

}
