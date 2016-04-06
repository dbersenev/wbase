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

import org.apache.commons.dbutils.ProxyFactory;
import org.molasdin.wbase.transaction.manager.Engine;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dbersenev on 15.10.2014.
 */
public class JdbcEngine implements InvocationHandler, Engine, AutoCloseable{
    private Connection connection;
    private Connection proxy;
    private List<Statement> statements = new LinkedList<Statement>();

    public JdbcEngine(Connection connection) {
        this.connection = connection;
        proxy = ProxyFactory.instance().createConnection(this);
    }

    public Connection connection(){
        return proxy;
    }

    public Connection realConnection(){
        return connection;
    }

    public ResultSet preparedQueryResult(String query, Object ...args) {
        try {
            PreparedStatement stmt = connection().prepareStatement(query);
            int pos = 1;
            for(Object entry: args) {
                stmt.setObject(pos, entry);
            }
            return stmt.executeQuery();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public ExtendedStatement extended(String query) {
        return new ExtendedStatement(prepared(query));
    }

    public ExtendedStatement extended(String query, ResultSetType type, ResultSetConcurrency conc, ResultSetHoldability hold) {
        return new ExtendedStatement(prepared(query, type, conc, hold));
    }

    public PreparedStatement prepared(String query, ResultSetType type, ResultSetConcurrency conc, ResultSetHoldability hold) {
        try {
            return connection().prepareStatement(query, type.value(), conc.value(), hold.value());
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public PreparedStatement prepared(String query) {
        try {
            return connection().prepareStatement(query);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void closeDependencies() throws Exception{
        for(Statement entry: statements){
            if(entry.isClosed()){
                entry.close();
            }
        }
        statements.clear();
    }

    public void close() {
        if(statements.isEmpty()){
            return;
        }
        try{
            closeDependencies();
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(proxy, args);
        if(method.getName().equals("prepareStatement") || method.getName().equals("createStatement")){
            statements.add((Statement) result);
        }
        return result;
    }
}
