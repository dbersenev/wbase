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

import org.molasdin.wbase.transaction.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Created by molasdin on 1/22/14.
 */

/**
 * Usage example:
 *
 * void method(){
 *  run(new Transactional<Connection, Void>(){
 *      Void run(Connection connection) throws Exception{
 *          Statement stmt = connection.createStatement();
 *          stmt.execute(sql);
 *          return null;
 *      }
 *  )
 *
 * }
 *
 */

public class JdbcTransactions {
    /**
     * Run jdbc aware code within simple transaction.
     * Uses DataSource to retrieve connection.
     * Cleanup actions are performed at the end of invocation
     * @param transactional
     * @param ds
     * @param <U>
     * @return
     */
    public static <U> U run(Transactional<Connection, U> transactional, DataSource ds){
        try{
            Connection cn = ds.getConnection();
            boolean ac = cn.getAutoCommit();
            try{
                cn.setAutoCommit(false);
                U result = transactional.run(cn);
                cn.commit();
                return result;
            }catch (Exception ex){
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(ac);
                cn.close();
            }
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
