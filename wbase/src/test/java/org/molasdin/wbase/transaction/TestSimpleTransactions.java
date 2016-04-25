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

import org.junit.Assert;
import org.junit.Test;
import org.molasdin.wbase.transaction.context.ExtendedUserTransaction;
import org.molasdin.wbase.transaction.context.GlobalContextHolder;
import org.molasdin.wbase.transaction.exceptions.TransactionCommittedException;
import org.molasdin.wbase.transaction.exceptions.TransactionRolledBackException;
import org.molasdin.wbase.transaction.manager.SimpleEngine;
import org.molasdin.wbase.transaction.manager.SimpleTransactionManager;

/**
 * Created by dbersenev on 19.04.2016.
 */
public class TestSimpleTransactions {
    private SimpleTransactionManager txm = new SimpleTransactionManager("KEY");

    @Test
    public void testRollback() {
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction()) {
            tx.rollback();
            Assert.assertEquals(true, tx.wasRolledBack());
        }
    }

    @Test
    public void testCommit() {
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction()) {
            tx.commit();
            Assert.assertEquals(true, tx.wasCommitted());
        }
    }

    @Test(expected = TransactionCommittedException.class)
    public void testMultipleCommits(){
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction()) {
            tx.commit();
            tx.commit();
        }
    }

    @Test(expected = TransactionRolledBackException.class)
    public void testMultipleRollbacks(){
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction()) {
            tx.rollback();
            tx.rollback();
        }
    }

    @Test(expected = TransactionCommittedException.class)
    public void testCommitRollback(){
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction()) {
            tx.commit();
            tx.rollback();
        }
    }

    @Test(expected = TransactionRolledBackException.class)
    public void testRollbackCommit(){
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction()) {
            tx.rollback();
            tx.commit();
        }
    }
}


