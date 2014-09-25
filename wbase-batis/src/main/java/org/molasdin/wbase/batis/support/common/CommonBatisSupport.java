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

package org.molasdin.wbase.batis.support.common;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.molasdin.wbase.batis.VoidMapper;
import org.molasdin.wbase.batis.support.AbstractBatisSupport;
import org.molasdin.wbase.transaction.Transactional;

/**
 * User: dbersenev
 * Date: 26.11.13
 * Time: 18:06
 */
public class CommonBatisSupport<M> extends AbstractBatisSupport<M> {

    private SqlSessionFactory factory;
    private Class<M> mapperClass;

    public CommonBatisSupport(SqlSessionFactory factory, Class<M> mapperClass) {
        this.factory = factory;
        this.mapperClass = mapperClass;
    }

    public SqlSession session() {
        return factory.openSession();
    }

    public SqlSession session(boolean autoCommit) {
        return factory.openSession(autoCommit);
    }

    public Class<M> mapperClass(){
        return mapperClass;
    }

    @Override
    public M mapper() {
        return session().getMapper(mapperClass);
    }

    @Override
    public Pair<SqlSession, M> sessionAndMapper() {
        SqlSession session = session();
        return Pair.of(session, session.getMapper(mapperClass));
    }

    public M mapper(SqlSession session){
        return session.getMapper(mapperClass);
    }

    public <T, U> U runCommon(SqlSession session, T ctx, Transactional<T, U> runner){
        try {
            U result = runner.run(ctx);
            session.commit();
            return result;
        } catch (RuntimeException ex) {
            session.rollback();
            throw ex;
        } catch (Exception ex) {
            session.rollback();
            throw new RuntimeException(ex);
        } finally {
            session.close();
        }
    }
}