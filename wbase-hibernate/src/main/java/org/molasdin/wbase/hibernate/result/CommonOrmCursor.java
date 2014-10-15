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

package org.molasdin.wbase.hibernate.result;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.molasdin.wbase.hibernate.OrmCursor;
import org.molasdin.wbase.storage.AbstractCursor;
import org.molasdin.wbase.storage.Cursor;
import org.molasdin.wbase.storage.SearchConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * Created by dbersenev on 24.02.14.
 */
public abstract class CommonOrmCursor<T, U> extends AbstractCursor<T> implements OrmCursor<T, U> {

    private SearchConfiguration<T,U> searchConfiguration;

    private SessionFactory sessionFactory;
    private TransactionTemplate txTemplate;

    public TransactionTemplate txTemplate() {
        return txTemplate;
    }

    public void setTxManager(PlatformTransactionManager txManager) {
        txTemplate = new TransactionTemplate(txManager);
        configureTemplate(txTemplate);
    }

    protected void configureTemplate(TransactionTemplate template){
        template.setReadOnly(true);
        template.setIsolationLevel(Isolation.READ_UNCOMMITTED.value());
    }

    public void setSearchConfiguration(SearchConfiguration<T, U> searchConfiguration) {
        this.searchConfiguration = searchConfiguration;
    }

    public SearchConfiguration<T,U> searchSpecification(){
        return searchConfiguration;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory sessionFactory(){
        return sessionFactory;
    }

    public String translateProperty(String original){
        if(searchConfiguration.propertiesPrefix() != null){
            original = searchConfiguration.propertiesPrefix().concat(".").concat(original);
        }

        if(searchConfiguration.baseProperty() == null){
            return original;
        }

        return original.replace(searchConfiguration.baseProperty().concat("."), "");
    }

    @Override
    public Cursor<T> copy() {
        return null;
    }

    public List<T> data(){
        return txTemplate.execute(new TransactionCallback<List<T>>() {
            @Override
            public List<T> doInTransaction(TransactionStatus transactionStatus) {
                return dataCallback(sessionFactory.getCurrentSession());
            }
        });
    }

    public long totalRecords(){
        return txTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus transactionStatus) {
                Long result = totalCallback(sessionFactory.getCurrentSession());
                return result != null?result:0;
            }
        });
    }

    public abstract List<T> dataCallback(Session session);
    public abstract Long totalCallback(Session session);
}
