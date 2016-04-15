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
    TransactionDescriptor descriptor();
    boolean hasTransaction();

    void setUnderline(Transaction tx);

    void setInterceptionMode(InterceptionMode mode);

    Interception interception();

    Transaction createTransaction();
}
