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
import org.molasdin.wbase.batis.support.BatisMapperEngine;
import org.molasdin.wbase.transaction.AbstractTransaction;
import org.molasdin.wbase.transaction.AbstractUserTransaction;
import org.molasdin.wbase.transaction.Transaction;

import java.sql.Savepoint;

/**
 * Created by dbersenev on 21.10.2014.
 */
public class BatisTransaction extends AbstractTransaction{
    private SqlSession session;
    private Savepoint savepoint;

    public BatisTransaction(SqlSession session) {
        this.session = session;
    }

    public void setSavepoint(Savepoint savepoint) {
        this.savepoint = savepoint;
    }

    @Override
    public void rollback() {
        if (savepoint != null) {
            try {
                session.getConnection().rollback(savepoint);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            session.rollback();
        }
        super.rollback();
    }

    @Override
    public void commit() {
        session.commit();
        super.commit();
    }

    @Override
    public void close() {
        try {
            if (savepoint != null) {
                session.getConnection().releaseSavepoint(savepoint);
                return;
            }
            session.close();
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
