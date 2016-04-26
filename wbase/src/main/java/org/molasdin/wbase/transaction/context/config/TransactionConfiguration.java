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

import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.TransactionDescriptor;
import org.molasdin.wbase.transaction.context.ResourceClosure;
import org.molasdin.wbase.transaction.context.interceptors.Interception;
import org.molasdin.wbase.transaction.context.interceptors.InterceptionMode;

import java.util.Objects;
import java.util.function.Function;

/**
 * Created by molasdin on 3/30/16.
 */
public interface TransactionConfiguration {
    <U> U resource(Object key);
    boolean hasResource(Object key);
    <U> void bindResource(Object key, U resource);
    <U> void bindResource(Object key, U resource, ResourceClosure<U> onClose);

    <U> void bindStableResource(Object key, U resource);
    <U> void bindStableResource(Object key, U resource, ResourceClosure<U> onClose);

    /**
     * Proxy function will be used to create some resource proxy when this configuration is applied
     * Resource must be present for this configuration
     * @param key - resource key
     * @param resourceClass - class of resource
     * @param proxyMaker - function to produce proxy
     * @param <U>
     */
    <U> void attachProxyFunction(Object key, Class<U> resourceClass, Function<U,U> proxyMaker);

    TransactionDescriptor descriptor();

    /**
     * If this configuration already has transaction retrieved by some criteria
     * @return
     */
    boolean hasTransaction();

    /**
     * Register transaction implementation
     * @param tx
     */
    void setUnderline(Transaction tx);

    /**
     * Enables resource synchronization and linking to transaction
     * which is original creator of the resource
     * @param key
     */
    void setSyncOnResource(Object key);

    /**
     * Interception mode allows different application of interceptors
     * When switched previous one is remembered (if no empty)
     * @param mode
     */
    void setInterceptionMode(InterceptionMode mode);

    Interception interception();

    /**
     * Produces eithe existing transaction (hasTransaction) or creates new one based on
     * the specified options
     * @return
     */
    Transaction createTransaction();
}
