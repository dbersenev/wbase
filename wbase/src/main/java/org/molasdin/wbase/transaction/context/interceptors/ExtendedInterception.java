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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by molasdin on 4/6/16.
 */
public class ExtendedInterception implements Interception{
    private List<Interceptor<TransactionEvent>> startListeners = new ArrayList<>();

    private List<Interceptor<TerminatableTransactionEvent>> preCommitListeners = new ArrayList<>();
    private List<Interceptor<TransactionEvent>> postCommitListeners = new ArrayList<>();

    private List<Interceptor<TransactionEvent>> preCloseListeners = new ArrayList<>();
    private List<Interceptor<TransactionEvent>> postCloseListeners = new ArrayList<>();

    private List<Interceptor<TerminatableTransactionEvent>> preRollbackListeners = new ArrayList<>();
    private List<Interceptor<TransactionEvent>> postRollbackListeners = new ArrayList<>();

    public void addStart(Interceptor<TransactionEvent> consumer) {
        startListeners.add(consumer);
    }
    public void emitStart(TransactionEvent event){
        invokeCommonListeners(event, startListeners);
    }

    public void addPreCommit(Interceptor<TerminatableTransactionEvent> consumer) {
        preCommitListeners.add(consumer);
    }
    public void emitPreCommit(TerminatableTransactionEvent event){
        invokeTerminatableListeners(event, preCommitListeners);
    }

    public void addPostCommit(Interceptor<TransactionEvent> consumer) {
        postCommitListeners.add(consumer);
    }
    public void emitPostCommit(TransactionEvent event){
        invokeCommonListeners(event,  postCommitListeners);
    }

    public void addPreRollback(Interceptor<TerminatableTransactionEvent> consumer) {
        preRollbackListeners.add(consumer);
    }
    public void emitPreRollback(TerminatableTransactionEvent event){
        invokeTerminatableListeners(event, preRollbackListeners);
    }

    public void addPostRollback(Interceptor<TransactionEvent> consumer) {
        postRollbackListeners.add(consumer);
    }
    public void emitPostRollback(TransactionEvent event){
        invokeCommonListeners(event, postRollbackListeners);
    }

    public void addPreClose(Interceptor<TransactionEvent> consumer) {
        preCloseListeners.add(consumer);
    }
    public void emitPreClose(TransactionEvent event){
        invokeCommonListeners(event, preCloseListeners);
    }

    public void addPostClose(Interceptor<TransactionEvent> consumer) {
        postCloseListeners.add(consumer);
    }
    public void emitPostClose(TransactionEvent event){
        invokeCommonListeners(event, postCloseListeners);
    }

    public void addFrom(ExtendedInterception interception) {
        startListeners.addAll(interception.startListeners);
        preCommitListeners.addAll(interception.preCommitListeners);
        postCommitListeners.addAll(interception.postCloseListeners);
        preRollbackListeners.addAll(interception.preRollbackListeners);
        postRollbackListeners.addAll(interception.postRollbackListeners);
        preCloseListeners.addAll(interception.preCloseListeners);
        postCloseListeners.addAll(interception.postCloseListeners);
    }

    private void invokeCommonListeners(TransactionEvent event, List<Interceptor<TransactionEvent>> interceptors) {
        for (Interceptor<TransactionEvent> entry : interceptors) {
            entry.intercept(event);
        }
    }

    private void invokeTerminatableListeners(TerminatableTransactionEvent event, List<Interceptor<TerminatableTransactionEvent>> interceptors) {
        for (Interceptor<TerminatableTransactionEvent> entry : interceptors) {
            entry.intercept(event);
        }
    }

}
