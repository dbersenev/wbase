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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.session.Configuration;
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
    private Class<M> mapperClass;

    public BatisTransactionProvider() {
    }

    public BatisTransactionProvider(SqlSessionFactory sessionFactory, Class<M> mapperClass) {
        this.sessionFactory = sessionFactory;
        this.mapperClass = mapperClass;
    }

    public void setMapperClass(Class<M> clazz){
        this.mapperClass = clazz;
    }

    @Override
    public Transaction<BatisEngine<M>> newTransaction(TransactionDescriptor descriptor) {
        TransactionIsolationLevel level = levelToBatisLevel(descriptor.isolation(), sessionFactory.getConfiguration());
        SqlSession session =  level != null?
                sessionFactory.openSession(level):
                sessionFactory.openSession();
        final M m = session.getMapper(mapperClass);
        return new BatisTransaction<M>(new BasicBatisEngine<M>(session, m), session);
    }

    @Override
    public BatisEngine<M> detachedEngine() {
        SqlSession session = sessionFactory.openSession();
        return new BasicBatisEngine<M>(session, session.getMapper(mapperClass));
    }

    private TransactionIsolationLevel levelToBatisLevel(TransactionIsolation isolation, Configuration configuration){
        if(isolation == null){
            return null;
        }
        if(StringUtils.containsIgnoreCase(configuration.getDatabaseId(), "oracle")){
            if(TransactionIsolation.READ_UNCOMMITTED.equals(isolation)){
                return null;
            }
        }

        for(TransactionIsolationLevel entry: TransactionIsolationLevel.values()){
            if(isolation.jdbcCode().equals(entry.getLevel())){
                return entry;
            }
        }
        return null;
    }
}
