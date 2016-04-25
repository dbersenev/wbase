/*
 * Copyright 2016 Bersenev Dmitry molasdin@outlook.com
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

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created by dbersenev on 21.04.2016.
 */
public class SqlSessionProxy implements SqlSession {

    private SqlSession session;

    public SqlSessionProxy(SqlSession session) {
        this.session = session;
    }

    @Override
    public <T> T selectOne(String s) {
        return session.selectOne(s);
    }

    @Override
    public <T> T selectOne(String s, Object o) {
        return session.selectOne(s, o);
    }

    @Override
    public <E> List<E> selectList(String s) {
        return session.selectList(s);
    }

    @Override
    public <E> List<E> selectList(String s, Object o) {
        return session.selectList(s, o);
    }

    @Override
    public <E> List<E> selectList(String s, Object o, RowBounds rowBounds) {
        return session.selectList(s, o, rowBounds);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, String s1) {
        return session.selectMap(s, s1);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, Object o, String s1) {
        return session.selectMap(s, o, s1);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, Object o, String s1, RowBounds rowBounds) {
        return session.selectMap(s, o, s1, rowBounds);
    }

    @Override
    public void select(String s, Object o, ResultHandler resultHandler) {
        session.select(s, o, resultHandler);
    }

    @Override
    public void select(String s, ResultHandler resultHandler) {
        session.select(s, resultHandler);
    }

    @Override
    public void select(String s, Object o, RowBounds rowBounds, ResultHandler resultHandler) {
        session.select(s, o, rowBounds, resultHandler);
    }

    @Override
    public int insert(String s) {
        return session.insert(s);
    }

    @Override
    public int insert(String s, Object o) {
        return session.insert(s, o);
    }

    @Override
    public int update(String s) {
        return session.update(s);
    }

    @Override
    public int update(String s, Object o) {
        return session.update(s, o);
    }

    @Override
    public int delete(String s) {
        return session.delete(s);
    }

    @Override
    public int delete(String s, Object o) {
        return session.delete(s, o);
    }

    @Override
    public void commit() {

    }

    @Override
    public void commit(boolean b) {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void rollback(boolean b) {

    }

    @Override
    public List<BatchResult> flushStatements() {
        return session.flushStatements();
    }

    @Override
    public void close() {

    }

    @Override
    public void clearCache() {
        session.clearCache();

    }

    @Override
    public Configuration getConfiguration() {
        return session.getConfiguration();
    }

    @Override
    public <T> T getMapper(Class<T> aClass) {
        return session.getMapper(aClass);
    }

    @Override
    public Connection getConnection() {
        return session.getConnection();
    }
}
