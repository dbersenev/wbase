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

import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.molasdin.wbase.batis.support.BasicBatisEngine;
import org.molasdin.wbase.batis.support.BatisEngine;
import org.molasdin.wbase.transaction.*;

/**
 * Created by dbersenev on 21.10.2014.
 */
public class BatisTransactionProvider<M> extends AbstractTransactionProvider<BatisEngine<M>> {
    private SqlSessionFactory sessionFactory;
    private SqlSession sqlSession;
    private M mapper;
    private Class<M> mapperClass;

    public BatisTransactionProvider() {
    }

    public BatisTransactionProvider(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setMapper(M mapper) {
        this.mapper = mapper;
    }

    public void setMapperClass(Class<M> clazz){
        this.mapperClass = clazz;
    }

    public void setSession(SqlSession session) {
        this.sqlSession = session;
    }

    @Override
    public Transaction<BatisEngine<M>> newTransaction(TransactionDescriptor descriptor) {
        SqlSession session = sqlSession;
        if(session == null){
            session =  descriptor.isolation() != null? sessionFactory.openSession(TransactionIsolationLevel.values()[descriptor.isolation().jdbcCode()]):
                    sessionFactory.openSession();
        }
        M m = mapper;
        if(m == null){
            m = session.getMapper(mapperClass);
        }
        final SqlSession finalSession = session;
        final M finalM = m;
        return new BatisTransaction<M>(new EngineFactory<BatisEngine<M>>() {
            @Override
            public BatisEngine<M> create() {
                return new BasicBatisEngine<M>(finalSession, finalM);
            }
        }, session);
    }
}
