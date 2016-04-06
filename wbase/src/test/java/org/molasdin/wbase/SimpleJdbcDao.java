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

import java.sql.Connection;
import java.util.Optional;

/**
 * Created by dbersenev on 16.10.2014.
 */
public class SimpleJdbcDao extends BasicSupport<JdbcEngine> {
    public void method(){
        run(tx -> {
                JdbcEngine engine = tx.engine();
                Connection conn = engine.connection();
                //Nested Transaction
                tx.invokeNested(txIn -> Optional.empty());
                return Optional.empty();
            });
    }

    public void method2(){
        Transaction<JdbcEngine> t = newTransaction();
        Connection connection = t.engine().connection();
        Transaction<JdbcEngine> inner = t.nested();
        inner.begin();
        //do something
        inner.commit();
        t.commitAndClose();
    }
}
