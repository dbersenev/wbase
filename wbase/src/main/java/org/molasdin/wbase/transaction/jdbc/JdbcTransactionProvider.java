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
import org.molasdin.wbase.transaction.profiles.ProfilesManager;

import java.sql.Connection;

/**
 * Created by dbersenev on 21.10.2014.
 */
public class JdbcTransactionProvider extends AbstractTransactionProvider<JdbcEngine> {

    private Source<Connection> connectionSource;

    public JdbcTransactionProvider(Source<Connection> connectionSource) {
        this.connectionSource = connectionSource;
    }

    @Override
    public Transaction<JdbcEngine> newTransaction(TransactionDescriptor descriptor) {
        final Connection connection = connectionSource.value();
        if(descriptor.isolation() != null){
            try {
                connection.setTransactionIsolation(descriptor.isolation().jdbcCode());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        try {
            Boolean autocommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            JdbcEngine engine = new JdbcEngine(connection);
            return new JdbcTransaction(engine, connection, autocommit);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
//        return new JdbcTransaction(connectionSource);
    }

    @Override
    public JdbcEngine detachedEngine() {
        return new JdbcEngine(connectionSource.value());
    }
}
