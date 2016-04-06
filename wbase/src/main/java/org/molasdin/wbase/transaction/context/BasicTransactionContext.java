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

import org.apache.commons.lang3.tuple.Pair;
import org.molasdin.wbase.transaction.*;
import org.molasdin.wbase.transaction.context.config.*;
import org.molasdin.wbase.transaction.manager.Engine;

import java.util.*;

/**
 * Created by molasdin on 2/1/16.
 */
public class BasicTransactionContext implements TransactionContext, ConfigurationCallback, UserConfigurationCallback {

    private Map<Object, Object> resources = new HashMap<>();

    private boolean isFresh = true;

    private Deque<Pair<TransactionDescriptor, Integer>> descriptors = new ArrayDeque<>();

    private ExtendedTransaction currentTransaction = null;

    private TransactionDescriptor newMergedDescriptor(TransactionDescriptor descr) {
        if (descriptors.isEmpty()) {
            descriptors.push(Pair.of(descr, 1));
        } else {
            Pair<TransactionDescriptor, Integer> tmp = descriptors.peek();
            if(tmp.getRight() == 0) {
                return tmp.getLeft();
            }
        }
        return descr;
    }


    public void modifyDescriptor(TransactionDescriptor descriptor) {
        descriptors.push(Pair.of(descriptor, 0));
    }

    public void restoreDescriptor() {
        if (descriptors.size() > 1) {
            descriptors.remove();
        }
    }

    @Override
    public <U extends Engine> UserTransactionConfiguration<U> newUserTransactionConfiguration(Object key, TransactionDescriptor desc, Object ...resourcesKeys) {
        TransactionDescriptor newDesc = newMergedDescriptor(desc);
        BasicUserTransactionConfiguration<U> cfg = new BasicUserTransactionConfiguration<>(key, newDesc, resources.containsKey(key), this);
        for(Object entry: resourcesKeys){
            if(resources.containsKey(entry)){
                cfg.localResources().put(entry, resources.get(key));
            }
        }
        return cfg;
    }

    @Override
    public Transaction configureTransaction(ExtendedConfiguration cfg) {

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Engine> UserTransaction<T> configureUserTransaction(ExtendedUserConfiguration<T> cfg) {
        if(cfg.hasTransaction() && !cfg.changed()){
            return ExtendedUserTransaction.class.cast(resources.get(cfg.key())).rollbackOnlyProxy();
        }

        ExtendedUserTransaction<T> tx = new ExtendedUserTransaction<>(cfg.underline().getLeft(), cfg.underline().getRight(), this);

        if(cfg.descriptor().requirement().equals(Requirement.ALWAYS_NEW_LINKED) && currentTransaction != null) {
            tx.interception().addPostRollback((t) -> currentTransaction.rollback());
        }

        for(Object entry: cfg.freshResources()) {
            if(resources.containsKey(entry)){
                tx.resourcesArchive().put(entry, resources.get(entry));
            } else {
                tx.resourcesToRemove().add(entry);
            }
            resources.put(entry, cfg.resource(entry));
        }

        if(cfg.hasTransaction()){
            tx.resourcesArchive().put(cfg.key(), tx);
        }

        resources.put(cfg.key(), tx);

        if(!tx.resourcesArchive().isEmpty()) {
            tx.interception().addPreClose((t) -> resources.putAll(t.currentTransaction().resourcesArchive()));
        }

        if(!tx.resourcesToRemove().isEmpty()) {
            tx.interception().addPreClose((t) -> resources.keySet().removeAll(t.currentTransaction().resourcesToRemove()));
        }

        if(isFresh){
            tx.interception().addPostClose((t) -> GlobalContextHolder.setSynchronization(null));
            isFresh = false;
        }

        currentTransaction = tx;

        tx.begin();

        return tx;
    }
}
