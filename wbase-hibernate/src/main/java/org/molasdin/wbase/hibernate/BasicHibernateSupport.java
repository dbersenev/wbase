/*
 * Copyright 2013 Bersenev Dmitry molasdin@outlook.com
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

package org.molasdin.wbase.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.molasdin.wbase.hibernate.transaction.BasicHibernateTransactionManager;
import org.molasdin.wbase.storage.BasicSupport;
import org.molasdin.wbase.transaction.manager.TransactionManager;

public class BasicHibernateSupport extends BasicSupport<HibernateEngine> implements HibernateSupport {

    private SessionFactory sessionFactory;


    public Session currentSession(){
        return sessionFactory().getCurrentSession();
    }

    public Session newSession(){
        return sessionFactory.openSession();
    }

    public void setSessionFactory(SessionFactory factory){
        this.sessionFactory = factory;
    }

    public SessionFactory sessionFactory(){
        return sessionFactory;
    }
    @Override
    public TransactionManager<HibernateEngine> newDefaultProvider() {
        BasicHibernateTransactionManager provider = new BasicHibernateTransactionManager();
        provider.setSessionFactory(sessionFactory);
        return provider;
    }
}
