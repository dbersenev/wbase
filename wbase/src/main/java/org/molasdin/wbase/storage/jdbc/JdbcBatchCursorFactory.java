/*
 * Copyright 2016 Bersenev Dmitry molasdin@outlook.com
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

import org.molasdin.wbase.storage.cursor.BatchCursor;
import org.molasdin.wbase.storage.cursor.BatchCursorFactory;
import org.molasdin.wbase.storage.cursor.EmptyCursor;
import org.molasdin.wbase.transaction.AttachedResource;
import org.molasdin.wbase.transaction.TransactionDescriptor;
import org.molasdin.wbase.transaction.TransactionDescriptors;
import org.molasdin.wbase.transaction.UserTransaction;
import org.molasdin.wbase.transaction.jdbc.*;
import org.molasdin.wbase.transaction.manager.TransactionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbersenev on 22.04.2016.
 */
public class JdbcBatchCursorFactory<T> implements BatchCursorFactory<T> {


    private TransactionManager<JdbcEngine> txm;
    private TransactionDescriptor descriptor = TransactionDescriptors.SIMPLE;
    private String query;
    private String countQuery;
    private List<Object> args = new ArrayList<>();
    private RsTranslator<T> translator;

    public JdbcBatchCursorFactory(TransactionManager<JdbcEngine> txm, String query, String countQuery, RsTranslator<T> translator) {
        this.txm = txm;
        this.query = query;
        this.countQuery = countQuery;
        this.translator = translator;
    }

    public void setTransactionDescriptor(TransactionDescriptor descriptor){
        this.descriptor = descriptor;
    }

    public void addArgument(Object arg){
        args.add(arg);
    }

    public void addArguments(List<?> args){
        this.args.addAll(args);
    }

    @Override
    public BatchCursor<T> newCursor(long pageSize) {
        UserTransaction<JdbcEngine> tx = null;
        try {
            tx = txm.createTransaction(descriptor);
            JdbcEngine e = tx.engine();
            long count = e.extended(countQuery).setObjects(1, args).longResult();
            if(count == 0) {
                tx.close();
                return EmptyCursor.emptyTwoWaysBatchCursor();
            } else {
                ExtendedStatement stmt = e.extended(query, ResultSetType.FORWARD_ONLY,
                        ResultSetConcurrency.READ_ONLY,
                        ResultSetHoldability.HOLD_CURSORS_OVER_COMMIT);
                stmt.setObjects(1, args);
                ResultSet rs = stmt.result();
                rs.setFetchSize((int)pageSize);
                return new JdbcBatchCursor<>(new AttachedResource<>(rs, tx), count, pageSize, translator);
            }
        } catch (Exception ex) {
            tx.close();
            throw new RuntimeException(ex);
        }
    }
}
