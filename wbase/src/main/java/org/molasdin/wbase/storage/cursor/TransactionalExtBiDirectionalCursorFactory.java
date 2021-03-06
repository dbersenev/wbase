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

package org.molasdin.wbase.storage.cursor;

import org.molasdin.wbase.transaction.TransactionDescriptors;
import org.molasdin.wbase.transaction.manager.Engine;
import org.molasdin.wbase.transaction.manager.TransactionManager;
import org.molasdin.wbase.transaction.TransactionDescriptor;

/**
 * Created by dbersenev on 03.03.2016.
 */
public abstract class TransactionalExtBiDirectionalCursorFactory<T, F extends Engine> extends AbstractExtBiDirectionalCursorFactory<T> {


    private TransactionManager<F> pm;
    private TransactionDescriptor desc = TransactionDescriptors.SIMPLE;

    public TransactionalExtBiDirectionalCursorFactory(TransactionManager<F> pm, TransactionDescriptor desc) {
        this.pm = pm;
        this.desc = desc;
    }

    public TransactionalExtBiDirectionalCursorFactory(TransactionManager<F> pm) {
        this.pm = pm;
    }

    public void setTransactionDescriptor(TransactionDescriptor desc){
        this.desc = desc;
    }

    public TransactionManager<F> processingManager(){
        return pm;
    }
    public TransactionDescriptor transactionDescriptor(){
        return desc;
    }
}
