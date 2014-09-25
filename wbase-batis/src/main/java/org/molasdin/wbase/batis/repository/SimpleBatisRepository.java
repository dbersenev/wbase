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

package org.molasdin.wbase.batis.repository;

import org.apache.ibatis.session.SqlSessionFactory;
import org.molasdin.wbase.batis.CommonMapper;
import org.molasdin.wbase.batis.support.common.CommonBatisSupport;
import org.molasdin.wbase.storage.*;

/**
 * Created by dbersenev on 04.02.14.
 */
public class SimpleBatisRepository<T extends Storable<T>,M extends CommonMapper<T>> extends BatisRepository<T,M> {

    public SimpleBatisRepository(SqlSessionFactory factory, Class<M> mapperClass) {
        super(new CommonBatisSupport<M>(factory, mapperClass));
    }

    public SimpleBatisRepository(SqlSessionFactory factory, Class<M> mapperClass, String mapperId) {
        this(factory, mapperClass);
        setMapperId(mapperId);
    }
}
