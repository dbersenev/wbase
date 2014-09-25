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

package org.molasdin.wbase.batis.spring.support;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.session.SqlSession;
import org.molasdin.wbase.batis.support.lite.LiteBatisSupport;
import org.molasdin.wbase.transaction.Transactional;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.Callable;

/**
 * Created by dbersenev on 15.09.2014.
 */
public class SpringBatisSupport<M> extends LiteBatisSupport<M> {
    private SqlSessionTemplate template;
    private Class<M> mapperClass;
    private TransactionTemplate transactionTemplate;

    private Callable<SqlSession> sessionSource;
    private Callable<M> mapperSource;
    private Callable<Pair<SqlSession, M>> sessionAndMapperSource;

    public void setTransactionManager(PlatformTransactionManager manager) {
        this.transactionTemplate = new TransactionTemplate(manager);
    }

    public void setTemplate(SqlSessionTemplate template) {
        this.template = template;
    }

    public void setMapper(Class<M> mapperClass) {
        this.mapperClass = mapperClass;
    }

    public void init() {
        sessionSource = new Callable<SqlSession>() {
            @Override
            public SqlSession call() throws Exception {
                return template;
            }
        };
        mapperSource = new Callable<M>() {
            @Override
            public M call() throws Exception {
                return template.getMapper(mapperClass);
            }
        };
        sessionAndMapperSource = new Callable<Pair<SqlSession, M>>() {
            @Override
            public Pair<SqlSession, M> call() throws Exception {
                return Pair.of((SqlSession) template, template.getMapper(mapperClass));
            }
        };
    }

    @Override
    public Callable<SqlSession> sessionSource() {
        return sessionSource;
    }

    @Override
    public Callable<M> mapperSource() {
        return mapperSource;
    }

    @Override
    public Callable<Pair<SqlSession, M>> sessionAndMapperSource() {
        return sessionAndMapperSource;
    }

    @Override
    public <T, U> U runCommon(SqlSession sqlSession, final T ctx, final Transactional<T, U> tuTransactional) {
        return transactionTemplate.execute(new TransactionCallback<U>() {
                                               @Override
                                               public U doInTransaction(TransactionStatus transactionStatus) {
                                                   try {
                                                       return tuTransactional.run(ctx);
                                                   } catch (Exception ex) {
                                                       throw new RuntimeException(ex);
                                                   }
                                               }
                                           }
        );
    }
}
