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
import org.molasdin.wbase.transaction.context.interceptors.*;
import org.molasdin.wbase.transaction.manager.Engine;

import java.util.*;

/**
 * Created by molasdin on 2/1/16.
 */
public class BasicTransactionContext implements TransactionContext, ConfigurationCallback, UserConfigurationCallback {

    private Map<Object, TransactionResource<?>> resources = new HashMap<>();

    private Map<Object, ExtendedTransaction> transactions = new HashMap<>();

    private Deque<Pair<TransactionDescriptor, Integer>> descriptors = new ArrayDeque<>();

    private ExtendedTransaction currentTransaction = null;

    private Set<ExtendedInterception> interceptions = new HashSet<>();

    private TransactionDescriptor newMergedDescriptor(TransactionDescriptor descr) {
        if (descriptors.isEmpty()) {
            descriptors.push(Pair.of(descr, 1));
        } else {
            Pair<TransactionDescriptor, Integer> tmp = descriptors.peek();
            if (tmp.getRight() == 0) {
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
    public TransactionConfiguration newTransactionConfiguration(Object key, TransactionDescriptor desc, Object... resourceKeys) {
        TransactionDescriptor newDesc = newMergedDescriptor(desc);
        return prepareConfig(key, new BasicTransactionConfiguration(key, newDesc, transactions.containsKey(key), this), resourceKeys);
    }

    @Override
    public <U extends Engine> UserTransactionConfiguration<U> newUserTransactionConfiguration(Object key, TransactionDescriptor desc, Object... resourcesKeys) {
        TransactionDescriptor newDesc = newMergedDescriptor(desc);
        return prepareConfig(key, new BasicUserTransactionConfiguration<>(key, newDesc, transactions.containsKey(key), this), resourcesKeys);
    }

    private <F extends ExtendedConfiguration> F prepareConfig(Object key, F cfg, Object... resourcesKeys) {
        for (Object entry : resourcesKeys) {
            if (resources.containsKey(entry)) {
                cfg.resources().put(entry, resources.get(key));
            }
        }
        return cfg;
    }

    @Override
    public Transaction configureTransaction(ExtendedConfiguration cfg) {
        if (cfg.hasTransaction() && !cfg.changed()) {
            return transactions.get(cfg.key()).rollbackOnlyProxy();
        }

        ExtendedTransaction tx = new ExtendedTransaction((Transaction) cfg.underline(), this);
        prepareTransaction(tx, cfg);
        return tx;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Engine> UserTransaction<T> configureUserTransaction(ExtendedUserConfiguration<T> cfg) {
        if (cfg.hasTransaction() && !cfg.changed()) {
            return ExtendedUserTransaction.class.cast(transactions.get(cfg.key())).rollbackOnlyProxy();
        }

        ExtendedUserTransaction<T> tx = new ExtendedUserTransaction<>(cfg.underline().getLeft(), cfg.underline().getRight(), this);
        prepareTransaction(tx, cfg);
        return tx;
    }

    private void prepareTransaction(ExtendedTransaction tx, ExtendedConfiguration cfg) {
        //interceptors configuration
        if (!cfg.interceptions().isEmpty()) {
            Set<ExtendedInterception> interceptionsToRemove = new HashSet<>();
            for (InterceptionMode mode : cfg.interceptions().keySet()) {
                ExtendedInterception tmp = cfg.interceptions().get(mode);
                //include current transaction to interceptor
                if (mode.equals(InterceptionMode.CURRENT) || mode.equals(InterceptionMode.ALL)) {
                    tx.interception().addInterception(tmp);
                }

                //prepare interceptor to be applied for all descendant transactions
                if (mode.equals(InterceptionMode.DESCENDANTS) || mode.equals(InterceptionMode.ALL)) {
                    interceptions.add(tmp);
                    interceptionsToRemove.add(tmp);
                }
            }
            //detach interceptors if transaction is no longer alive
            if (!interceptionsToRemove.isEmpty()) {
                tx.interception().addPostClose((e) -> {
                    interceptions.removeAll(interceptionsToRemove);
                    for (ExtendedInterception entry : interceptionsToRemove) {
                        entry.detach();
                    }
                });
            }
            for (ExtendedInterception entry : interceptions) {
                tx.interception().addInterception(entry);
            }
        }

        //linked requirement configuration
        if (cfg.descriptor().requirement().equals(Requirement.ALWAYS_NEW_LINKED) && currentTransaction != null) {
            tx.interception().addPostRollback((t) -> currentTransaction.rollback());
            currentTransaction.interception().addPreRollback((e) -> tx.rollback());
            currentTransaction.interception().addPreClose(((e) -> tx.close()));
        }

        //resources configuration
        Map<Object, TransactionResource<?>> resourcesArchive = new HashMap<>();

        //unstable resources will cause closing all of the nested transaction which are using them
        Set<Object> resourcesToRemove = new HashSet<>();
        //stable resources will be alive until last transaction removes them
        Set<Object> resourcesToRemoveStable = new HashSet<>();

        for (Object entry : cfg.resources().keySet()) {
            if (cfg.freshResources().contains(entry)) {
                if (resources.containsKey(entry)) {
                    resourcesArchive.put(entry, resources.get(entry));
                }
                resources.put(entry, cfg.resources().get(entry));
                if (!cfg.resources().get(entry).isStable()) {
                    resourcesToRemove.add(entry);
                }
            }

            if (resources.get(entry).isStable()) {
                resourcesToRemoveStable.add(entry);
            }

            resources.get(entry).clients().add(tx);
        }

        if (!resourcesArchive.isEmpty()) {
            tx.interception().addPreClose((t) -> {
                resourcesArchive.keySet().retainAll(resources.keySet());
                resources.putAll(resourcesArchive);
            });
        }
        if (!resourcesToRemove.isEmpty()) {
            tx.interception().addPreClose((e) -> {
                for (Object entry : resourcesToRemove) {
                    try (TransactionResource<?> tr = resources.get(entry)) {
                        boolean generatingTx = false;
                        for (ExtendedTransaction t : tr.clients()) {
                            if (t.equals(tx)) {
                                generatingTx = true;
                            }
                            t.close();
                        }
                        if (generatingTx) {
                            resources.remove(entry);
                        }
                    }

                }
            });
        }

        if (!resourcesToRemoveStable.isEmpty()) {
            tx.interception().addPreClose((e) -> {
                for (Object entry : resourcesToRemoveStable) {
                    TransactionResource<?> res = resources.get(entry);
                    if (res.clients().contains(e.currentTransaction()) && res.clients().size() == 1) {
                        res.close();
                    }
                    res.clients().remove(e.currentTransaction());
                }
            });
        }

        //backup old transaction if managed by the same manager
        if (cfg.hasTransaction()) {
            ExtendedTransaction archTx = transactions.get(cfg.key());
            Object archTxKey = cfg.key();
            tx.interception().addPreClose((e) -> {
                if (transactions.containsKey(archTxKey)) {
                    transactions.put(archTxKey, archTx);
                }
            });
        }
        transactions.put(cfg.key(), tx);

        //universal clean up interceptor
        //invoked on last transaction close
        tx.interception().addPostClose((e) -> {
            if (transactions.size() == 1 && transactions.containsKey(e.currentTransaction())) {
                GlobalContextHolder.setSynchronization(null);
            }
        });

        currentTransaction = tx;

        tx.begin();
    }
}
