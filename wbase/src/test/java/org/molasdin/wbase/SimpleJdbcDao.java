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

package org.molasdin.wbase;

import org.molasdin.wbase.storage.BasicSupport;
import org.molasdin.wbase.transaction.jdbc.JdbcEngine;
import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.Transactional;

import java.sql.Connection;

/**
 * Created by dbersenev on 16.10.2014.
 */
public class SimpleJdbcDao extends BasicSupport<JdbcEngine> {
    public void method(){
        run(new Transactional<JdbcEngine, Void>() {
            @Override
            public Void run(Transaction<JdbcEngine> context) throws Exception {
                JdbcEngine engine = context.engine();
                Connection conn = engine.connection();
                //Nested Transaction
                context.invokeNested(new Transactional<JdbcEngine, Integer>() {
                    @Override
                    public Integer run(Transaction<JdbcEngine> context) throws Exception {
                        return null;
                    }
                });
                return null;
            }
        });
    }

    public void method2(){
        Transaction<JdbcEngine> t = newTransaction();
        t.begin();
        Connection connection = t.engine().connection();
        Transaction<JdbcEngine> inner = t.nested();
        inner.begin();
        //do something
        inner.commit();
        t.commitAndClose();
    }
}
