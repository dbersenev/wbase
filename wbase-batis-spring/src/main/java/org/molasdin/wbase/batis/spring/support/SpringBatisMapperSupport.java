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
import org.molasdin.wbase.batis.spring.transaction.BatisSpringTransactionManager;
import org.molasdin.wbase.batis.support.BatisMapperEngine;
import org.molasdin.wbase.batis.support.CommonBatisMapperSupport;
import org.molasdin.wbase.spring.transaction.SpringTransactionManager;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Created by dbersenev on 15.09.2014.
 */
public class SpringBatisMapperSupport extends CommonBatisMapperSupport {
    private SqlSessionTemplate template;
    private PlatformTransactionManager manager;

    public void setTransactionManager(PlatformTransactionManager manager) {
        this.manager = manager;
        init();
    }

    public void setTemplate(SqlSessionTemplate template) {
        this.template = template;
        init();
    }

    @Override
    public SqlSessionFactory sessionFactory() {
        return template.getSqlSessionFactory();
    }

    @Override
    public SqlSession session() {
        return template;
    }

    void init() {
        if(template == null || manager == null){
            return;
        }
        SpringTransactionManager<BatisMapperEngine> provider = new BatisSpringTransactionManager(template);
        provider.setTransactionManagerCommon(manager);
        setDefaultTransactionProvider(provider);
    }


}
