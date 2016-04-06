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

package org.molasdin.wbase.transaction.manager;

import org.molasdin.wbase.transaction.UserTransaction;
import org.molasdin.wbase.transaction.*;
import org.molasdin.wbase.transaction.context.GlobalContextHolder;
import org.molasdin.wbase.transaction.context.TransactionContext;
import org.molasdin.wbase.transaction.context.config.UserTransactionConfiguration;
import org.molasdin.wbase.transaction.exceptions.TransactionPropagationException;

/**
 * Created by dbersenev on 28.10.2014.
 */
public abstract class AbstractTransactionManager<T extends Engine> implements TransactionManager<T> {

    private final static Object[] EMPTY_ARRAY = new Object[0];

    @Override
    public UserTransaction<T> createTransaction(TransactionDescriptor descriptor) {
        TransactionContext ctx = GlobalContextHolder.context();
        UserTransactionConfiguration<T> cfg = ctx.newUserTransactionConfiguration(this, descriptor, resourceKeys());
        if(!cfg.hasTransaction()){
            try {
                configure(cfg);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return cfg.createTransaction();
    }

    protected Object[] resourceKeys() {
        return EMPTY_ARRAY;
    }

    protected abstract void configure(UserTransactionConfiguration<T> cfg) throws Exception;

    protected void throwIfPropagationRequired(TransactionDescriptor descr){
        if(descr.requirement().equals(Requirement.PROPAGATED_ONLY)) {
            throw new TransactionPropagationException();
        }
    }
}
