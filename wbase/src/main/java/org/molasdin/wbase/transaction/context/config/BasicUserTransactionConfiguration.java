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

import org.apache.commons.lang3.tuple.Pair;
import org.molasdin.wbase.transaction.*;
import org.molasdin.wbase.transaction.context.config.*;
import org.molasdin.wbase.transaction.context.interceptors.ExtendedInterception;
import org.molasdin.wbase.transaction.context.interceptors.InterceptionMode;
import org.molasdin.wbase.transaction.context.interceptors.TerminatableTransactionEvent;
import org.molasdin.wbase.transaction.context.interceptors.TransactionEvent;
import org.molasdin.wbase.transaction.manager.Engine;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by dbersenev on 29.03.2016.
 */
public class BasicUserTransactionConfiguration<T extends Engine> implements UserTransactionConfiguration<T>, ExtendedUserConfiguration<T>{

    private Map<Object, Object> localResources = new HashMap<>();
    private Set<Object> newResources = new HashSet<>();
    private TransactionDescriptor descriptor;
    private boolean hasTx;
    private Object key;
    private Pair<Transaction, T> underline = null;

    private Map<InterceptionMode, ExtendedInterception> interceptions = new EnumMap<>(InterceptionMode.class);
    private InterceptionMode currentInterceptionMode = InterceptionMode.DESCENDANTS;

    private UserConfigurationCallback listener;

    public BasicUserTransactionConfiguration(Object key, TransactionDescriptor descriptor, boolean hasTx, UserConfigurationCallback listener) {
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
        return (U) localResources.get(key);
    }

    @Override
    public boolean hasResource(Object key) {
        return localResources.containsKey(key);
    }

    @Override
    public void bindResource(Object key, Object resource) {
        if(!localResources.containsKey(key) || resource.equals(localResources.get(key))) {
            localResources.put(key, resource);
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
    public void setUnderline(T engine, Transaction newTx) {
        if(engine == null || newTx == null){
            throw new RuntimeException();
        }
        underline = Pair.of(newTx, engine);
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
    public Map<Object, Object> resources() {
        return localResources;
    }


    @Override
    public boolean hasTransaction() {
        return hasTx;
    }

    @Override
    public Pair<Transaction, T> underline() {
        return underline;
    }

    @Override
    public UserTransaction<T> createTransaction() {
        return  listener.configureUserTransaction(this);
    }
}
