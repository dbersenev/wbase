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
import org.molasdin.wbase.transaction.manager.SimpleEngine;
import org.molasdin.wbase.transaction.manager.SimpleTransactionManager;
import org.molasdin.wbase.transaction.manager.TestResource;

/**
 * Created by molasdin on 4/19/16.
 */
public class TestInnerTransactions {

    private SimpleTransactionManager txm = new SimpleTransactionManager();

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
            try (UserTransaction<SimpleEngine> inner = txm.createTransaction(TransactionDescriptors.INSTANCE.alwaysNew())) {
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
            try (UserTransaction<SimpleEngine> inner = txm.createTransaction(TransactionDescriptors.INSTANCE.alwaysNew())) {
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
            try (UserTransaction<SimpleEngine> inner = txm.createTransaction(TransactionDescriptors.INSTANCE.alwaysNewLinked())) {
                inner.rollback();
            }
            Assert.assertTrue(tx.wasRolledBack());
        }
    }
}
