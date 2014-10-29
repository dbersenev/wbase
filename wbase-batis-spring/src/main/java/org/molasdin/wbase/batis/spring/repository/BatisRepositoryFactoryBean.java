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

package org.molasdin.wbase.batis.spring.repository;

import org.apache.commons.beanutils.ConstructorUtils;
import org.molasdin.wbase.batis.CommonMapper;
import org.molasdin.wbase.batis.repository.BatisRepository;
import org.molasdin.wbase.batis.spring.support.SpringBatisSupport;
import org.molasdin.wbase.batis.support.BatisSupport;
import org.molasdin.wbase.storage.Repository;
import org.molasdin.wbase.storage.Storable;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Created by dbersenev on 15.09.2014.
 */
public class BatisRepositoryFactoryBean<T extends Storable<T>, M extends CommonMapper<T>, F extends Repository<T>> implements FactoryBean<F> {

    private SqlSessionTemplate template;
    private Class<M> mapperClass;
    private PlatformTransactionManager txManager;
    private Class<? extends BatisRepository<T,M>> repositoryClass;
    private String mapperId;

    public void setTemplate(SqlSessionTemplate template) {
        this.template = template;
    }

    public void setMapperClass(Class<M> mapperClass) {
        this.mapperClass = mapperClass;
    }

    public void setTxManager(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }

    public void setRepositoryClass(Class<? extends BatisRepository<T,M>> repositoryClass) {
        this.repositoryClass = repositoryClass;
    }

    public void setMapperId(String mapperId) {
        this.mapperId = mapperId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public F getObject() throws Exception {
        SpringBatisSupport<M> support = new SpringBatisSupport<M>(mapperClass);
        support.setTemplate(template);
        support.setTransactionManager(txManager);
        BatisRepository<T,M> repo = ConstructorUtils.invokeExactConstructor(repositoryClass, new Object[]{support},
                new Class[]{BatisSupport.class});
        repo.setMapperId(mapperId);
        return (F)repo;
    }

    @Override
    public Class<?> getObjectType() {
        return repositoryClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
