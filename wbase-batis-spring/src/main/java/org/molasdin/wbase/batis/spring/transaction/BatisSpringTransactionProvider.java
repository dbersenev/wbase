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

package org.molasdin.wbase.batis.spring.transaction;

import org.apache.ibatis.session.SqlSession;
import org.molasdin.wbase.batis.support.BasicBatisEngine;
import org.molasdin.wbase.batis.support.BatisEngine;
import org.molasdin.wbase.spring.transaction.SpringTransactionProvider;
import org.molasdin.wbase.transaction.EngineFactory;
import org.mybatis.spring.SqlSessionTemplate;

/**
 * Created by molasdin on 10/29/14.
 */
public class BatisSpringTransactionProvider<M> extends SpringTransactionProvider<BatisEngine<M>> {
    private SqlSession sharedSession;
    private Class<M> mapperClass;

    public BatisSpringTransactionProvider() {
    }

    public BatisSpringTransactionProvider(SqlSessionTemplate sharedSession, Class<M> mapperClass) {
        this.sharedSession = sharedSession;
        this.mapperClass = mapperClass;
    }

    public void setSharedSession(SqlSession sharedSession) {
        this.sharedSession = sharedSession;
    }

    public void setMapperClass(Class<M> mapperClass) {
        this.mapperClass = mapperClass;
    }

    @Override
    public BatisEngine<M> newEngine() {
        return new BasicBatisEngine<M>(sharedSession, sharedSession.getMapper(mapperClass));
    }

    @Override
    public BatisEngine<M> detachedEngine() {
        return newEngine();
    }
}
