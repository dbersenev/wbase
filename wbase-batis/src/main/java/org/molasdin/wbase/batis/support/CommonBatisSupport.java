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

package org.molasdin.wbase.batis.support;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.molasdin.wbase.batis.transaction.BatisTransactionProviderFactory;
import org.molasdin.wbase.storage.BasicSupport;
import org.molasdin.wbase.transaction.TransactionProviderFactory;

/**
 * User: dbersenev
 * Date: 26.11.13
 * Time: 18:06
 */
public class CommonBatisSupport<M> extends BasicSupport<BatisEngine<M>> implements BatisSupport<M> {

    private SqlSessionFactory factory;
    private Class<M> mapperClass;

    public CommonBatisSupport(Class<M> mapperClass) {
        this.mapperClass = mapperClass;
    }

    public CommonBatisSupport(SqlSessionFactory factory, Class<M> mapperClass) {
        setSessionFactory(factory);
        this.mapperClass = mapperClass;
    }

    public void setSessionFactory(SqlSessionFactory factory){
        this.factory = factory;
    }
    public SqlSessionFactory sessionFactory(){
        return factory;
    }
    @Override
    public TransactionProviderFactory<BatisEngine<M>> newDefaultFactory() {
        return new BatisTransactionProviderFactory<M>(factory, mapperClass);
    }

    public SqlSession session() {
        return factory.openSession();
    }

    public Class<M> mapperClass(){
        return mapperClass;
    }

    @Override
    public M mapper() {
        return session().getMapper(mapperClass);
    }




}