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

package org.molasdin.wbase.transaction.context.config;

import org.apache.commons.collections4.Closure;
import org.apache.commons.lang3.tuple.Pair;
import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.TransactionDescriptor;
import org.molasdin.wbase.transaction.UserTransaction;
import org.molasdin.wbase.transaction.context.BasicTransactionResource;
import org.molasdin.wbase.transaction.context.ResourceClosure;
import org.molasdin.wbase.transaction.context.TransactionResource;
import org.molasdin.wbase.transaction.context.interceptors.ExtendedInterception;
import org.molasdin.wbase.transaction.context.interceptors.InterceptionMode;

import java.util.*;

/**
 * Created by dbersenev on 12.04.2016.
 */
public class BasicTransactionConfiguration implements ExtendedConfiguration {
    private Map<Object, TransactionResource<?>> localResources = new HashMap<>();
    private Set<Object> newResources = new HashSet<>();
    private TransactionDescriptor descriptor;
    private boolean hasTx;
    private Object key;
    private Transaction underline = null;

    private Map<InterceptionMode, ExtendedInterception> interceptions = new EnumMap<>(InterceptionMode.class);
    private InterceptionMode currentInterceptionMode = InterceptionMode.DESCENDANTS;

    private ConfigurationCallback listener;

    protected BasicTransactionConfiguration(Object key, TransactionDescriptor descriptor, boolean hasTx) {
        this.key = key;
        this.descriptor = descriptor;
        this.hasTx = hasTx;
    }

    public BasicTransactionConfiguration(Object key, TransactionDescriptor descriptor, boolean hasTx, ConfigurationCallback listener) {
        this.key = key;
        this.descriptor = descriptor;
        this.hasTx = hasTx;
        this.listener = listener;
    }

    @Override
    public ExtendedInterception interception() {
        if(!interceptions.containsKey(currentInterceptionMode)) {
            setInterceptionMode(currentInterceptionMode);
        }
        return interceptions.get(currentInterceptionMode);
    }

    @Override
    public void setInterceptionMode(InterceptionMode mode) {
        if(!interceptions.containsKey(mode)){
            interceptions.put(mode, new ExtendedInterception());
        }
        currentInterceptionMode = mode;
    }

    @Override
    public Map<InterceptionMode, ExtendedInterception> interceptions() {
        return interceptions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> U resource(Object key) {
        return (U) localResources.get(key).resource();
    }

    @Override
    public boolean hasResource(Object key) {
        return localResources.containsKey(key);
    }

    @Override
    public <U> void bindStableResource(Object key, U resource) {
       bindResource(key, resource, null, true);
    }

    @Override
    public <U> void bindResource(Object key, U resource) {
        bindResource(key, resource, null, false);
    }

    @Override
    public <U> void bindResource(Object key, U resource, ResourceClosure<U> onClose) {
        bindResource(key, resource, onClose, false);
    }

    @Override
    public <U> void bindStableResource(Object key, U resource, ResourceClosure<U> onClose) {
        bindResource(key, resource, onClose, true);
    }

    private <U> void bindResource(Object key, U resource, ResourceClosure<U> onClose, boolean stable) {
        if(!localResources.containsKey(key) && !resource.equals(localResources.get(key))) {
            localResources.put(key, new BasicTransactionResource<>(resource, stable, onClose));
            newResources.add(key);
        }
    }

    @Override
    public Set<Object> freshResources() {
        return newResources;
    }

    @Override
    public TransactionDescriptor descriptor() {
        return descriptor;
    }


    @Override
    public void setUnderline(Transaction newTx) {
        if(newTx == null){
            throw new RuntimeException();
        }
        underline = newTx;
    }

    @Override
    public boolean changed(){
        return (underline != null) || !newResources.isEmpty();
    }

    @Override
    public Object key() {
        return key;
    }

    @Override
    public Map<Object, TransactionResource<?>> resources() {
        return localResources;
    }


    @Override
    public boolean hasTransaction() {
        return hasTx;
    }

    @Override
    public Object underline() {
        return underline;
    }

    @Override
    public Transaction createTransaction() {
        return  listener.configureTransaction(this);
    }
}
