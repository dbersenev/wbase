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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dbersenev on 15.10.2014.
 */
public class JdbcContext {
    private Connection connection;
    private List<Statement> statements = new LinkedList<Statement>();

    public JdbcContext(Connection connection) {
        this.connection = connection;
    }

    public Connection connection(){
        return connection;
    }

    public PreparedStatement preparedStatement(String sql){
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
        statements.add(stmt);
        return stmt;
    }

    public Statement statement(){
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        statements.add(statement);
        return statement;
    }

    protected void closeDependencies() throws Exception{
        for(Statement entry: statements){
            if(entry.isClosed()){
                entry.close();
            }
        }
    }
}
