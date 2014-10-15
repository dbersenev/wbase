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

package org.molasdin.wbase.storage.jdbc;

import org.molasdin.wbase.transaction.*;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Created by dbersenev on 15.10.2014.
 */
public class JdbcTransactionRunner implements TransactionRunner<JdbcContext> {
    private Connection connection;

    public JdbcTransactionRunner(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void setIsolation(TransactionIsolation isolation) {
        try{
            connection.setTransactionIsolation(isolationTo(isolation));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public <U> U invoke(Transactional<JdbcContext> transactional) {
        try {
            JdbcContext engine = new JdbcContext(connection);
            TransactionContext<JdbcContext> ctx= new BasicTransactionContext<JdbcContext>(engine){
                @Override
                public void rollback() {
                    try{
                        connection.rollback();
                    } catch (Exception ex){
                        throw new RuntimeException(ex);
                    }
                    throw new TransactionInterruptedException();
                }
            };
            try {
                return transactional.run(ctx);
            }catch (TransactionInterruptedException ex){
                return null;
            } finally {
                engine.closeDependencies();
                connection.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Integer isolationTo(TransactionIsolation isolation){
        switch (isolation){
            case NONE:
                return Connection.TRANSACTION_NONE;
            case READ_COMMITTED:
                return Connection.TRANSACTION_READ_COMMITTED;
            case READ_UNCOMMITTED:
                return Connection.TRANSACTION_READ_UNCOMMITTED;
            case REPEATABLE_READ:
                return Connection.TRANSACTION_REPEATABLE_READ;
            case SERIALIZABLE:
                return Connection.TRANSACTION_SERIALIZABLE;
        }
        return 0;
    }
}
