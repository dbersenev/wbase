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
import org.molasdin.wbase.transaction.manager.Engine;

/**
 * Created by dbersenev on 29.03.2016.
 */
public class BasicUserTransactionConfiguration<T extends Engine> extends BasicTransactionConfiguration implements ExtendedUserConfiguration<T>{
    private Pair<Transaction, T> underline = null;

    private UserConfigurationCallback listener;

    public BasicUserTransactionConfiguration(Object key, TransactionDescriptor descriptor, boolean hasTx, UserConfigurationCallback listener) {
        super(key, descriptor, hasTx);
        this.listener = listener;
    }

    @Override
    public void setUnderline(T engine, Transaction newTx) {
        if(engine == null || newTx == null){
            throw new RuntimeException();
        }
        underline = Pair.of(newTx, engine);
    }

    @Override
    public Pair<Transaction, T> underline() {
        return underline;
    }

    @Override
    public UserTransaction<T> createTransaction() {
        prepare();
        return  listener.configureUserTransaction(this);
    }
}
