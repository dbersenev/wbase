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

import org.apache.commons.collections4.bag.CollectionBag;
import org.apache.commons.lang3.tuple.Pair;
import org.molasdin.wbase.transaction.*;
import org.molasdin.wbase.transaction.context.config.*;
import org.molasdin.wbase.transaction.context.interceptors.*;
import org.molasdin.wbase.transaction.exceptions.TransactionNotConfiguredException;
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
                cfg.resources().put(entry, resources.get(entry));
            }
        }
        return cfg;
    }

    private boolean isNewRequired(ExtendedConfiguration cfg) {
        return !cfg.hasTransaction() || cfg.changed()
                || cfg.descriptor().requirement().hasNewSemantics()
                || cfg.descriptor().requirement().equals(Requirement.NESTED);
    }

    @Override
    public Transaction configureTransaction(ExtendedConfiguration cfg) {
        if (!isNewRequired(cfg)) {
            return transactions.get(cfg.key()).rollbackOnlyProxy();
        }

        if (cfg.underline() == null) {
            throw new TransactionNotConfiguredException();
        }

        ExtendedTransaction tx = new ExtendedTransaction((Transaction) cfg.underline(), this);
        prepareTransaction(tx, cfg);
        return tx;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Engine> UserTransaction<T> configureUserTransaction(ExtendedUserConfiguration<T> cfg) {
        if (!isNewRequired(cfg)) {
            return ExtendedUserTransaction.class.cast(transactions.get(cfg.key())).rollbackOnlyProxy();
        }

        if (cfg.underline() == null) {
            throw new TransactionNotConfiguredException();
        }

        ExtendedUserTransaction<T> tx = new ExtendedUserTransaction<>(cfg.underline().getLeft(), cfg.underline().getRight(), this);
        prepareTransaction(tx, cfg);
        return tx;
    }

    private void prepareTransaction(ExtendedTransaction tx, ExtendedConfiguration cfg) {
        //interceptors configuration
        Set<ExtendedInterception> interceptionsToRemove = new HashSet<>();
        if (!cfg.interceptions().isEmpty()) {
            Interception descInt = null;
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
                    if (mode.equals(InterceptionMode.DESCENDANTS)) {
                        descInt = tmp;
                    }
                }
            }

            for (ExtendedInterception entry : interceptions) {
                if (!entry.equals(descInt)) {
                    tx.interception().addInterception(entry);
                }
            }
        }

        //linked requirement configuration
        if (cfg.descriptor().requirement().equals(Requirement.ALWAYS_NEW_LINKED) && currentTransaction != null) {
            ExtendedTransaction currentTemp = currentTransaction;
            tx.interception().addPostRollback((e) -> currentTemp.rollback());
            currentTransaction.interception().addPreRollback((e) -> {
                if (!tx.wasCommitted() && !tx.wasRolledBack()) {
                    tx.rollback();
                }
            });
            currentTransaction.interception().addPreClose(((e) -> tx.close()));
        }

        ResourcesCleanupInterceptor resCleanup = new ResourcesCleanupInterceptor(resources);

        Object syncOnRes = cfg.syncOnResource();

        for (Object entry : cfg.resources().keySet()) {
            if (cfg.freshResources().contains(entry)) {
                if (resources.containsKey(entry)) {
                    resCleanup.archive().put(entry, resources.get(entry));
                }
                resources.put(entry, cfg.resources().get(entry));
                if (!cfg.resources().get(entry).isStable()) {
                    resCleanup.toRemove().add(entry);
                }
            } else {
                if (entry.equals(syncOnRes)) {
                    for (ExtendedTransaction cl : resources.get(entry).clients()) {
                        tx.interception().addPostRollback((e) -> {
                            cl.rollback();
                        });
                    }
                }
            }

            if (resources.get(entry).isStable()) {
                resCleanup.toRemoveStable().add(entry);
            }

            resources.get(entry).clients().add(tx);
        }

        resCleanup.used().putAll(cfg.resources());

        tx.interception().addPreClose(resCleanup);

        //retrieve transaction identified by the same key
        ExtendedTransaction archTx = cfg.hasTransaction() ? transactions.get(cfg.key()) : null;
        transactions.put(cfg.key(), tx);

        Object key = cfg.key();

        tx.interception().addPostClose((e) -> {
            //detach context if last transaction is running
            if (archTx == null && transactions.size() == 1 && transactions.containsKey(key)) {
                GlobalContextHolder.setSynchronization(null);
            }
            //restore archived transaction if present
            if (archTx != null && transactions.containsKey(key)) {
                transactions.put(key, archTx);
            } else {
                transactions.remove(key);
            }
        });

        //detach interceptors if transaction is no longer alive
        if (!interceptionsToRemove.isEmpty()) {
            tx.interception().addPostClose((e) -> {
                interceptions.removeAll(interceptionsToRemove);
                for (ExtendedInterception entry : interceptionsToRemove) {
                    entry.detach();
                }
            });
        }

        currentTransaction = tx;

        tx.begin();
    }
}
