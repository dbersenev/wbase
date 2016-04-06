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
import org.molasdin.wbase.hibernate.BasicHibernateEngine;
import org.molasdin.wbase.hibernate.HibernateEngine;
import org.molasdin.wbase.transaction.context.config.UserTransactionConfiguration;
import org.molasdin.wbase.transaction.manager.AbstractTransactionManager;

/**
 * Created by dbersenev on 14.01.2015.
 */
public class BasicHibernateTransactionManager extends AbstractTransactionManager<HibernateEngine> {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected void configure(UserTransactionConfiguration<HibernateEngine> cfg) {
        Session session = null;
        if(!cfg.hasResource(sessionFactory)) {
            cfg.bindResource(sessionFactory, sessionFactory.openSession());
        }
        session = cfg.resource(sessionFactory);
        cfg.setUnderline(new BasicHibernateEngine(session), new HibernateTransaction(session, cfg.descriptor().isolation().jdbcCode()));
    }

    @Override
    protected Object[] resourceKeys() {
        return new Object[]{sessionFactory};
    }
}
