/*
 * Copyright 2014 Bersenev Dmitry molasdin@outlook.com
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

package org.molasdin.wbase.batis.transaction;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.molasdin.wbase.Source;
import org.molasdin.wbase.batis.support.BasicBatisMapperEngine;
import org.molasdin.wbase.batis.support.BatisMapperEngine;
import org.molasdin.wbase.transaction.*;
import org.molasdin.wbase.transaction.context.config.UserTransactionConfiguration;
import org.molasdin.wbase.transaction.jdbc.proxy.ConnectionDelegate;
import org.molasdin.wbase.transaction.manager.AbstractTransactionManager;
import org.molasdin.wbase.transaction.profiles.ProfilesManager;
import org.molasdin.wbase.transaction.profiles.TransactionProfile;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by dbersenev on 21.10.2014.
 */
public class BatisTransactionManager extends AbstractTransactionManager<BatisMapperEngine> {
    private SqlSessionFactory sessionFactory;

    private Source<Connection> connectionSource;

    public BatisTransactionManager() {
    }

    public BatisTransactionManager(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public void setConnectionSource(Source<Connection> connectionSource) {
        this.connectionSource = connectionSource;
    }

    @Override
    protected void configure(UserTransactionConfiguration<BatisMapperEngine> cfg) {
        SqlSession session = null;
        TransactionDescriptor descriptor = cfg.descriptor();
        boolean hasSavePoint = descriptor.requirement().equals(Requirement.NESTED);
        boolean newRequired = cfg.descriptor().requirement().hasNewSemantics();
        if (!newRequired && cfg.hasResource(sessionFactory)) {
            session = cfg.resource(sessionFactory);
            cfg.setSyncOnResource(sessionFactory);
        } else if (!newRequired && connectionSource != null && cfg.hasResource(connectionSource.key())) {
            Connection tmp = cfg.resource(connectionSource.key());
            cfg.setSyncOnResource(connectionSource.key());
            SqlSession s = sessionFactory.openSession(tmp);
            cfg.bindResource(sessionFactory, s, SqlSession::close);
            session = cfg.resource(connectionSource.key());
            cfg.attachProxyFunction(sessionFactory, SqlSession.class, SqlSessionProxy::new);
        } else {
            throwIfPropagationRequired(descriptor);
            TransactionIsolationLevel level = levelToBatisLevel(descriptor.isolation(), sessionFactory.getConfiguration());
            SqlSession s = level != null ? sessionFactory.openSession(level) : sessionFactory.openSession();
            cfg.bindResource(sessionFactory, s, SqlSession::close);
            session = cfg.resource(sessionFactory);
            cfg.attachProxyFunction(sessionFactory, SqlSession.class, SqlSessionProxy::new);
            if(connectionSource != null) {
                cfg.bindResource(connectionSource.key(), session.getConnection());
                cfg.attachProxyFunction(connectionSource.key(), Connection.class, ConnectionDelegate::new);
            }
            hasSavePoint = false;
        }

        BatisTransaction tx = new BatisTransaction(session);

        if(hasSavePoint){
            try {
                tx.setSavepoint(session.getConnection().setSavepoint());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        cfg.setUnderline(new BasicBatisMapperEngine(session), tx);
    }

    @Override
    protected Object[] resourceKeys() {
        if(sessionFactory != null && connectionSource == null){
            return new Object[]{sessionFactory};
        } else if (sessionFactory == null && connectionSource != null){
            return new Object[]{connectionSource.key()};
        }
        return new Object[]{sessionFactory, connectionSource.key()};
    }

    private TransactionIsolationLevel levelToBatisLevel(TransactionIsolation isolation, Configuration configuration) {
        if (isolation == null) {
            return null;
        }

        TransactionProfile profile = ProfilesManager.INSTANCE.profileFor(configuration.getDatabaseId());
        isolation = profile.properIsolation(isolation);

        if (isolation == null) {
            return null;
        }

        for (TransactionIsolationLevel entry : TransactionIsolationLevel.values()) {
            if (isolation.jdbcCode().equals(entry.getLevel())) {
                return entry;
            }
        }
        return null;
    }
}
