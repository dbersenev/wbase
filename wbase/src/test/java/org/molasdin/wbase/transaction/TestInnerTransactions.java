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
import org.molasdin.wbase.transaction.exceptions.TransactionPropagationRequiredException;
import org.molasdin.wbase.transaction.manager.SimpleEngine;
import org.molasdin.wbase.transaction.manager.SimpleTransactionManager;
import org.molasdin.wbase.transaction.manager.TestResource;
import org.molasdin.wbase.transaction.manager.TransactionManager;

/**
 * Created by molasdin on 4/19/16.
 */
public class TestInnerTransactions {

    private SimpleTransactionManager txm = new SimpleTransactionManager("KEY");

    @Test
    public void initialTest() throws Exception {
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction()) {
            Assert.assertEquals(ExtendedUserTransaction.class, tx.getClass());
            try (UserTransaction<SimpleEngine> inner = txm.createTransaction()) {
                Assert.assertEquals(tx.engine(), inner.engine());
            }
            tx.commit();
        }
        Assert.assertTrue(GlobalContextHolder.isEmpty());
    }

    @Test
    public void testNew(){
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction()) {
            try (UserTransaction<SimpleEngine> inner = txm.createTransaction(TransactionDescriptors.ALWAYS_NEW)) {
                Assert.assertNotEquals(tx.engine(), inner.engine());
                inner.commit();
            }
            tx.commit();
        }
    }

    @Test
    public void testIfResourceRestored(){
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction()) {
            TestResource res = tx.engine().resource();
            try (UserTransaction<SimpleEngine> inner = txm.createTransaction(TransactionDescriptors.ALWAYS_NEW)) {
                Assert.assertNotEquals(tx.engine(), inner.engine());
                inner.commit();
            }
            try (UserTransaction<SimpleEngine> inner = txm.createTransaction()) {
                Assert.assertEquals(tx.engine(), inner.engine());
                Assert.assertEquals(res, inner.engine().resource());
            }
            tx.commit();
        }
    }

    @Test
    public void checkIfInnerRolledBackOuter(){
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction()) {
            try (UserTransaction<SimpleEngine> inner = txm.createTransaction()) {
                inner.rollback();
            }
            Assert.assertTrue(tx.wasRolledBack());
        }
    }

    @Test
    public void checkIfLinkedRolledBackOuter(){
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction()) {
            try (UserTransaction<SimpleEngine> inner = txm.createTransaction(TransactionDescriptors.ALWAYS_NEW_LINKED)) {
                inner.rollback();
            }
            Assert.assertTrue(tx.wasRolledBack());
        }
    }

    @Test
    public void checkPropagatedWithSharedResourceCommit(){
        TransactionManager<SimpleEngine> txm2 = new SimpleTransactionManager("KEY");
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction()) {
            try (UserTransaction<SimpleEngine> inner = txm2.createTransaction()) {
                inner.commit();
                Assert.assertTrue(inner.wasCommitted());
            }
            Assert.assertFalse(tx.wasCommitted());
            tx.commit();
        }
    }

    @Test
    public void checkPropagatedWithSharedResourceRollback(){
        TransactionManager<SimpleEngine> txm2 = new SimpleTransactionManager("KEY");
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction()) {
            try (UserTransaction<SimpleEngine> inner = txm2.createTransaction()) {
                inner.rollback();
                Assert.assertTrue(inner.wasRolledBack());
            }
            Assert.assertTrue(tx.wasRolledBack());
        }
    }

    @Test
    public void checkDescriptor(){
        TransactionManager<SimpleEngine> txm2 = new SimpleTransactionManager("KEY");
        TransactionDescriptor descr = TransactionDescriptors.NEW_OR_PROPAGATED;
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction(descr)) {
            tx.context().modifyDescriptor(TransactionDescriptors.ALWAYS_NEW);
            try (UserTransaction<SimpleEngine> inner = txm2.createTransaction(TransactionDescriptors.PROPAGATED_ONLY)) {
                inner.rollback();
                Assert.assertTrue(inner.wasRolledBack());
            }
            tx.context().restoreDescriptor();
            tx.commit();
            Assert.assertTrue(tx.wasCommitted());
        }
    }

    @Test
    public void checkRequiredPropagation(){
        TransactionManager<SimpleEngine> txm2 = new SimpleTransactionManager("KEY");
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction()) {
            tx.context().modifyDescriptor(TransactionDescriptors.PROPAGATED_ONLY);
            try (UserTransaction<SimpleEngine> inner = txm2.createTransaction(TransactionDescriptors.ALWAYS_NEW)) {
                inner.commit();
                Assert.assertTrue(inner.wasCommitted());
            }
            tx.context().restoreDescriptor();
            tx.commit();
            Assert.assertTrue(tx.wasCommitted());
        }
    }

    @Test(expected = TransactionPropagationRequiredException.class)
    public void checkPropagationError(){
        try (UserTransaction<SimpleEngine> tx = txm.createTransaction(TransactionDescriptors.PROPAGATED_ONLY)) {
            tx.commit();
        }
    }
}
