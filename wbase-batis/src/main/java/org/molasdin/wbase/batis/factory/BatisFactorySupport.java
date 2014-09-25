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

package org.molasdin.wbase.batis.factory;

import org.apache.ibatis.session.SqlSessionFactory;
import org.molasdin.wbase.batis.Constants;
import org.molasdin.wbase.registry.RegistryManager;

/**
 * Created by dbersenev on 04.02.14.
 */

/**
 * Simplifies creation of Repositories and DAOs.
 * By default it uses registry to retrieve SqlSessionFactory.
 * Use SqlSessionFactoryListener or BatisUtil to configure SqlSessionFactory.
 * @param <T>
 */
public abstract class BatisFactorySupport<T> {
    private T instance;

    private String sqlSessionFactoryName = Constants.SQL_SESSION_FACTORY.name();

    public void setSqlSessionFactoryName(String sqlSessionFactoryName) {
        this.sqlSessionFactoryName = sqlSessionFactoryName;
    }

    public abstract T configure(SqlSessionFactory sessionFactory);

    public T instance(){
        if(instance != null){
            return instance;
        }
        synchronized (this){
            if(instance != null){
                return instance;
            }
            instance = makeInstance();
            return instance;
        }
    }

    private T makeInstance(){
        return configure(sqlSessionFactory());
    }

    /**
     * This method can be overridden to change the way
     * SqlSessionFactory is located
     * @return
     */
    public SqlSessionFactory sqlSessionFactory(){
        return RegistryManager.INSTANCE.currentRegistry().item(sqlSessionFactoryName, SqlSessionFactory.class);
    }
}
