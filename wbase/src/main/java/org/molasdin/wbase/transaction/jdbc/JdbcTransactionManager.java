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

package org.molasdin.wbase.transaction.jdbc;

import org.molasdin.wbase.Source;
import org.molasdin.wbase.transaction.*;
import org.molasdin.wbase.transaction.context.config.UserTransactionConfiguration;
import org.molasdin.wbase.transaction.jdbc.proxy.ConnectionDelegate;
import org.molasdin.wbase.transaction.jdbc.proxy.ProtectedConnection;
import org.molasdin.wbase.transaction.manager.AbstractTransactionManager;

import java.sql.Connection;
import java.sql.Savepoint;

/**
 * Created by dbersenev on 21.10.2014.
 */
public class JdbcTransactionManager extends AbstractTransactionManager<JdbcEngine> {

    private Source<Connection> connectionSource;

    public JdbcTransactionManager(Source<Connection> connectionSource) {
        this.connectionSource = connectionSource;
    }

    @Override
    protected void configure(UserTransactionConfiguration<JdbcEngine> cfg) throws Exception {
        boolean isSavePoint = cfg.descriptor().requirement().equals(Requirement.NESTED);

        Object key = connectionSource.key();

        if (!cfg.hasResource(key) || cfg.descriptor().requirement().hasNewSemantics()) {
            throwIfPropagationRequired(cfg.descriptor());
            cfg.bindResource(key, connectionSource.value(), Connection::close);
            isSavePoint = false;
            cfg.attachProxyFunction(key, Connection.class, ProtectedConnection::new);
        } else {
            cfg.setSyncOnResource(key);
        }

        Connection connection = cfg.resource(key);

        int isolation = connection.getTransactionIsolation();
        if (cfg.descriptor().isolation() != null && !isSavePoint) {
            connection.setTransactionIsolation(cfg.descriptor().isolation().jdbcCode());
        }
        JdbcEngine engine = new JdbcEngine(connection);
        JdbcTransaction tx = null;
        if (isSavePoint) {
            Savepoint sp = connection.setSavepoint();
            tx = new JdbcTransaction(connection, sp);
        } else {
            Boolean autocommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            tx = new JdbcTransaction(connection, autocommit);
        }
        tx.setIsolation(isolation);
        cfg.setUnderline(engine, tx);
        cfg.interception().addPreClose((e) -> engine.close());
    }
}
