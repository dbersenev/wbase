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

package org.molasdin.wbase.batis.transaction;

import org.apache.ibatis.session.SqlSession;
import org.molasdin.wbase.batis.support.BatisEngine;
import org.molasdin.wbase.transaction.AbstractTransaction;
import org.molasdin.wbase.transaction.EngineFactory;
import org.molasdin.wbase.transaction.Transaction;

import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * Created by dbersenev on 21.10.2014.
 */
public class BatisTransaction<M> extends AbstractTransaction<BatisEngine<M>> {
    private SqlSession session;
    private Savepoint savepoint;

    public BatisTransaction(EngineFactory<BatisEngine<M>> engineFactory, SqlSession session) {
        super(engineFactory);
        this.session = session;
    }

    protected void setSavepoint(Savepoint savepoint) {
        this.savepoint = savepoint;
    }

    @Override
    public void begin() {

    }

    @Override
    public void rollback() {
        if(savepoint != null){
            try {
                session.getConnection().rollback(savepoint);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            session.rollback();
        }
    }

    @Override
    public void commit() {
        session.commit();

    }

    @Override
    public void close() {
        session.close();
    }

    @Override
    public Transaction<BatisEngine<M>> nested() {
        BatisTransaction<M> nested = new BatisTransaction<M>(engineFactory(), session);
        nested.setNested(true);
        Savepoint tmp = null;
        try {
            tmp = session.getConnection().setSavepoint();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        nested.setSavepoint(tmp);
        return nested;
    }
}
