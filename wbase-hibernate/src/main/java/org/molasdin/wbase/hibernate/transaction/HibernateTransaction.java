/*
 * Copyright 2015 Bersenev Dmitry molasdin@outlook.com
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

package org.molasdin.wbase.hibernate.transaction;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.molasdin.wbase.hibernate.HibernateEngine;
import org.molasdin.wbase.transaction.AbstractTransaction;
import org.molasdin.wbase.transaction.AbstractUserTransaction;

/**
 * Created by dbersenev on 14.01.2015.
 */
public class HibernateTransaction extends AbstractTransaction {

    private Transaction transaction;
    private Session session;
    private int isolation;
    private int newIsolation;

    public HibernateTransaction(Session session, int newIsolation) {
        this.session = session;
        this.newIsolation = newIsolation;
    }

    @Override
    public void begin() {
        session.doWork(connection -> {
            isolation = connection.getTransactionIsolation();
            connection.setTransactionIsolation(newIsolation);
        });
        transaction = session.getTransaction();
    }

    public void setIsolation(int isolation) {
        this.isolation = isolation;
    }

    @Override
    public void rollback() {
        transaction.rollback();
        super.rollback();
    }

    @Override
    public void commit() {
        if(FlushMode.MANUAL.equals(session.getFlushMode())) {
            session.flush();
        }
        transaction.commit();
        super.commit();
    }

    @Override
    public void close() {
        session.doWork((c) -> c.setTransactionIsolation(isolation));
        session.close();
    }
}
