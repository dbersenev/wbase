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

import org.molasdin.wbase.transaction.context.interceptors.Interceptor;
import org.molasdin.wbase.transaction.context.interceptors.TransactionEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by dbersenev on 20.04.2016.
 */
class ResourcesCleanupInterceptor implements Interceptor<TransactionEvent> {

    private Map<Object, TransactionResource<?>> resources;

    private Set<Object> toRemove = new HashSet<>();
    private Set<Object> toRemoveStable = new HashSet<>();
    private Map<Object, TransactionResource<?>> archive = new HashMap<>();
    private Map<Object, TransactionResource<?>> used = new HashMap<>();

    /**
     *
     * @param resources - global resources map
     */
    ResourcesCleanupInterceptor(Map<Object, TransactionResource<?>> resources) {
        this.resources = resources;
    }

    /**
     * Resources to remove from global storage
     * @return
     */
    Set<Object> toRemove() {
        return toRemove;
    }

    /**
     * Stable resources to remove from global storage.
     * Removed by the last transaction
     * @return
     */
    Set<Object> toRemoveStable() {
        return toRemoveStable;
    }

    /**
     * Resources to restore after transaction
     * @return
     */
    Map<Object, TransactionResource<?>> archive() {
        return archive;
    }

    /**
     * All resources used by the transaction
     * @return
     */
    Map<Object, TransactionResource<?>> used() {
        return used;
    }

    @Override
    public void intercept(TransactionEvent e) {
        //retain only what is left to restore
        archive.keySet().retainAll(resources.keySet());
        //traverse all resources used by the transaction
        for (Object entry : used.keySet()) {
            TransactionResource<?> tr = used.get(entry);
            //check if resource must be removed by this transaction
            if (toRemove.contains(entry)) {
                try {
                    for (ExtendedTransaction t : tr.clients()) {
                        if (t.equals(e.currentTransaction())) {
                            resources.remove(entry);
                        } else {
                            t.close();
                        }
                    }
                } finally {
                    tr.close();
                }

            } else {
                //check if it is stable resource
                if(toRemoveStable.contains(entry)) {
                    if (tr.clients().contains(e.currentTransaction()) && tr.clients().size() == 1) {
                        tr.close();
                    }
                }
                //remove current resource client
                tr.clients().remove(e.currentTransaction());
            }
        }
        //restore what was archived
        resources.putAll(archive);
    }
}
