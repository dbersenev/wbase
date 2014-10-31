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
import org.molasdin.wbase.hibernate.transaction.HibernateTransactionProvider;
import org.molasdin.wbase.spring.transaction.SpringTransactionProvider;
import org.molasdin.wbase.storage.BasicSupport;
import org.molasdin.wbase.transaction.TransactionProvider;
import org.springframework.transaction.PlatformTransactionManager;

public class BasicHibernateSupport extends BasicSupport<HibernateEngine> implements HibernateSupport {

    private SessionFactory sessionFactory;

    private HibernateCursorFactory cursorFactory;

    private PlatformTransactionManager tx;

    public void setTransactionManager(PlatformTransactionManager tx) {
        this.tx = tx;
    }

    public void setCursorFactory(HibernateCursorFactory resultFactory) {
        this.cursorFactory = resultFactory;
    }

    public Session currentSession(){
        return factory().getCurrentSession();
    }

    public Session newSession(){
        return sessionFactory.openSession();
    }

    public void setSessionFactory(SessionFactory factory){
        this.sessionFactory = factory;
    }

    public SessionFactory factory(){
        return sessionFactory;
    }

    @Override
    public HibernateCursorFactory resultFactory() {
        return cursorFactory;
    }

    @Override
    public TransactionProvider<HibernateEngine> newDefaultProvider() {
        SpringTransactionProvider<HibernateEngine> provider = new HibernateTransactionProvider(sessionFactory);
        provider.setTransactionManager(tx);
        return provider;
    }

    public void init(){
        if(cursorFactory == null){
            cursorFactory = new BasicHibernateCursorFactory(transactionProvider());
        }
    }

    public HibernateEngine currentEngine(){
        return transactionProvider().detachedEngine();
    }

}
