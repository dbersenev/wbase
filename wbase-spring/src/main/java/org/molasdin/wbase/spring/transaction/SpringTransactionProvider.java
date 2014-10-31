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

package org.molasdin.wbase.spring.transaction;

import org.molasdin.wbase.Source;
import org.molasdin.wbase.transaction.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Created by dbersenev on 28.10.2014.
 */
public abstract class SpringTransactionProvider<T extends Engine> extends AbstractTransactionProvider<T> {
    private PlatformTransactionManager tx;

    public SpringTransactionProvider() {
    }

    public SpringTransactionProvider(PlatformTransactionManager tx) {
        this.tx = tx;
    }

    public void setTransactionManager(PlatformTransactionManager tx) {
        this.tx = tx;
    }

    @Override
    public Transaction<T> newTransaction(TransactionDescriptor descriptor) {
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        if (descriptor.isolation() != null) {
            def.setIsolationLevel(descriptor.isolation().jdbcCode());
        }
        TransactionStatus status = tx.getTransaction(def);

        return new SpringTransaction<T>(newEngine(), tx, status);
    }

    public abstract T newEngine();

}
