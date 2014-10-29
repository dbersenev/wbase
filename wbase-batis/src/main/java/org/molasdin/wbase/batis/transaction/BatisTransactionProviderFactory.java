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

import org.apache.ibatis.session.SqlSessionFactory;
import org.molasdin.wbase.batis.support.BatisEngine;
import org.molasdin.wbase.transaction.TransactionProvider;
import org.molasdin.wbase.transaction.TransactionProviderFactory;

/**
 * Created by dbersenev on 21.10.2014.
 */
public class BatisTransactionProviderFactory<M> implements TransactionProviderFactory<BatisEngine<M>>{
    private SqlSessionFactory sessionFactory;
    private Class<M> mapperClass;

    public BatisTransactionProviderFactory() {
    }

    public BatisTransactionProviderFactory(SqlSessionFactory sessionFactory, Class<M> mapperClass) {
        setSessionFactory(sessionFactory);
        setMapperClass(mapperClass);
    }

    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setMapperClass(Class<M> mapperClass) {
        this.mapperClass = mapperClass;
    }

    @Override
    public TransactionProvider<BatisEngine<M>> createProvider() {
        BatisTransactionProvider<M> provider = new BatisTransactionProvider<M>(sessionFactory);
        provider.setMapperClass(mapperClass);
        return provider;
    }
}
