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

import org.molasdin.wbase.batis.CommonMapper;
import org.molasdin.wbase.batis.support.BatisMapperEngine;
import org.molasdin.wbase.batis.support.BatisMapperSupport;
import org.molasdin.wbase.storage.*;
import org.molasdin.wbase.transaction.Transaction;
import org.molasdin.wbase.transaction.TransactionDescriptor;
import org.molasdin.wbase.transaction.UserTransaction;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Created by dbersenev on 13.03.14.
 */
public class BatisRepository<T, M extends CommonMapper<T>, K extends Serializable> implements Repository<T, K> {

    private BatisMapperSupport support;
    private Class<M> mapperClass;


    public BatisMapperSupport support() {
        return support;
    }

    public BatisRepository(BatisMapperSupport support, Class<M> mapperClass) {
        this.support = support;
        this.mapperClass = mapperClass;
    }

    @Override
    public Optional<T> byId(final K id) {
        try(UserTransaction<BatisMapperEngine> tx = support().newTransaction()){
            T r = tx.engine().mapper(mapperClass).findById(id);
            tx.commit();
            return Optional.ofNullable(r);
        }
    }

    @Override
    public void save(final T o) {
        try(UserTransaction<BatisMapperEngine> tx = support().newTransaction()){
            tx.engine().mapper(mapperClass).save(o);
            tx.commit();
        }
    }

    @Override
    public void update(final T o) {
        try(UserTransaction<BatisMapperEngine> tx = support().newTransaction()){
            tx.engine().mapper(mapperClass).update(o);
            tx.commit();
        }
    }

    @Override
    public void remove(final T o) {
        try(UserTransaction<BatisMapperEngine> tx = support().newTransaction()){
            tx.engine().mapper(mapperClass).remove(o);
            tx.commit();
        }
    }
}
