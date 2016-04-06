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

package org.molasdin.wbase.spring.transaction.jdbc;

import org.molasdin.wbase.spring.transaction.SpringTransactionManager;
import org.molasdin.wbase.transaction.context.TransactionContext;
import org.molasdin.wbase.transaction.jdbc.JdbcEngine;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;

/**
 * Created by molasdin on 3/21/16.
 */
public class SpringJdbcTransactionManager extends SpringTransactionManager<JdbcEngine> {

    private DataSource ds;

    @Required
    public void setDataSource(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public JdbcEngine newEngine() {
        return new JdbcEngine(DataSourceUtils.getConnection(ds));
    }

}
