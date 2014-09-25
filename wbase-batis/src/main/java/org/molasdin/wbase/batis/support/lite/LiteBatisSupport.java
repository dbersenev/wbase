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

package org.molasdin.wbase.batis.support.lite;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.session.SqlSession;
import org.molasdin.wbase.batis.support.AbstractBatisSupport;
import org.molasdin.wbase.transaction.Transactional;

import java.util.concurrent.Callable;

/**
 * Created by dbersenev on 13.03.14.
 */
public abstract class LiteBatisSupport<M> extends AbstractBatisSupport<M> {

    public abstract Callable<SqlSession> sessionSource();
    public abstract Callable<M> mapperSource();
    public abstract Callable<Pair<SqlSession, M>> sessionAndMapperSource();

    @Override
    public SqlSession session() {
        try{
            return sessionSource().call();
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public M mapper() {
        try{
            return mapperSource().call();
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Pair<SqlSession, M> sessionAndMapper() {
        try{
            return sessionAndMapperSource().call();
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
