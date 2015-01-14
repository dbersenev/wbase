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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.molasdin.wbase.hibernate.BasicHibernateEngine;
import org.molasdin.wbase.hibernate.HibernateEngine;
import org.molasdin.wbase.transaction.AbstractTransactionProvider;
import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.TransactionDescriptor;
import org.molasdin.wbase.transaction.TransactionDescriptors;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by dbersenev on 14.01.2015.
 */
public class BasicHibernateTransactionProvider extends AbstractTransactionProvider<HibernateEngine> {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Transaction<HibernateEngine> newTransaction(final TransactionDescriptor descriptor) {
        Session session = sessionFactory.openSession();
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                connection.setTransactionIsolation(descriptor.isolation().jdbcCode());
            }
        });
        org.hibernate.Transaction transaction = session.beginTransaction();
        return new BasicHibernateTransaction(new BasicHibernateEngine(session), transaction);
    }
}
