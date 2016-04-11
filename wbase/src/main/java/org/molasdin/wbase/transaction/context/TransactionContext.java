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

import org.molasdin.wbase.transaction.context.config.TransactionConfiguration;
import org.molasdin.wbase.transaction.context.config.UserTransactionConfiguration;
import org.molasdin.wbase.transaction.manager.Engine;
import org.molasdin.wbase.transaction.TransactionDescriptor;

/**
 * Created by molasdin on 1/30/16.
 */
public interface TransactionContext extends UserTransactionContext {
    TransactionConfiguration newTransactionConfiguration(Object key, TransactionDescriptor desc, Object ...resourceKeys);
    <U extends Engine> UserTransactionConfiguration<U> newUserTransactionConfiguration(Object key, TransactionDescriptor desc, Object ...resourceKeys);
}
