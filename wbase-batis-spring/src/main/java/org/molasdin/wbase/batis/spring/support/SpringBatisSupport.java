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

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.molasdin.wbase.batis.support.BasicBatisEngine;
import org.molasdin.wbase.batis.support.BatisEngine;
import org.molasdin.wbase.batis.support.CommonBatisSupport;
import org.molasdin.wbase.spring.transaction.SpringTransactionProviderFactory;
import org.molasdin.wbase.transaction.EngineFactory;
import org.molasdin.wbase.transaction.TransactionProviderFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Created by dbersenev on 15.09.2014.
 */
public class SpringBatisSupport<M> extends CommonBatisSupport<M> {
    private SqlSessionTemplate template;
    private PlatformTransactionManager manager;

    public SpringBatisSupport(Class<M> mapperClass) {
        super(mapperClass);
    }

    public void setTransactionManager(PlatformTransactionManager manager) {
        this.manager = manager;
    }

    public void setTemplate(SqlSessionTemplate template) {
        this.template = template;
    }

    @Override
    public SqlSessionFactory sessionFactory() {
        return template.getSqlSessionFactory();
    }

    @Override
    public SqlSession session() {
        return template;
    }

    @Override
    public TransactionProviderFactory<BatisEngine<M>> newDefaultFactory() {
        return new SpringTransactionProviderFactory<BatisEngine<M>>(manager, new EngineFactory<BatisEngine<M>>() {
            @Override
            public BatisEngine<M> create() {
                return new BasicBatisEngine<M>(template, mapper());
            }
        });
    }


}
