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

package org.molasdin.wbase.transaction;

/**
 * Created by molasdin on 1/31/16.
 */
public class DelegatingTransaction implements Transaction {

    private Transaction tx = null;

    public DelegatingTransaction(Transaction tx) {
        this.tx = tx;
    }

    public Transaction original(){
        return tx;
    }

    @Override
    public void rollback() {
        tx.rollback();
    }

    @Override
    public void commit() {
        tx.commit();
    }

    @Override
    public void close() {
        tx.close();
    }

    @Override
    public boolean wasRolledBack() {
        return tx.wasRolledBack();
    }

    @Override
    public boolean wasCommitted() {
        return tx.wasCommitted();
    }
}
