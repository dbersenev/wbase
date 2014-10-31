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
import org.molasdin.wbase.transaction.AbstractTransaction;
import org.molasdin.wbase.transaction.EngineFactory;
import org.molasdin.wbase.transaction.Transaction;

import java.sql.Connection;
import java.sql.Savepoint;

/**
 * Created by dbersenev on 16.10.2014.
 */
public class JdbcTransaction extends AbstractTransaction<JdbcEngine> {
    private Savepoint savepoint;
    private Connection connection;
    private Boolean autocommit;
    private Source<Connection> source;

   /* public JdbcTransaction(Source<Connection> source) {
        this.source = source;
    }*/

    public JdbcTransaction(JdbcEngine engine, Connection connection, Boolean autocommit) {
        super(engine);
        this.connection = connection;
        this.autocommit = autocommit;
    }

    public void setSavepoint(Savepoint savepoint) {
        this.savepoint = savepoint;
    }

    @Override
    public void commit() {
        try {
            connection.commit();
            if(!isNested()){
                connection.setAutoCommit(autocommit);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void rollback() {
        try {
            if(savepoint != null){
                connection.rollback(savepoint);
            } else{
                connection.rollback();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
        super.close();
        if(isNested()){
            return;
        }
        try{
            engine().close();
            if(connection.isClosed()){
                return;
            }
            connection.close();
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Transaction<JdbcEngine> nested() {
        JdbcTransaction nested = new JdbcTransaction(engine(), connection, autocommit);
        nested.setNested(true);
        try {
            nested.setSavepoint(connection.setSavepoint());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return nested;
    }
}
