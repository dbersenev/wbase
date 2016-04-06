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

import org.molasdin.wbase.transaction.*;
import org.molasdin.wbase.transaction.context.TransactionContext;
import org.molasdin.wbase.transaction.manager.AbstractTransactionManager;
import org.molasdin.wbase.transaction.manager.Engine;
import org.molasdin.wbase.transaction.manager.TransactionManager;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Created by dbersenev on 28.10.2014.
 */
public abstract class SpringTransactionManager<T extends Engine> implements TransactionManager<T> {
    private PlatformTransactionManager tx;

    @Required
    public void setTransactionManager(PlatformTransactionManager tx) {
        this.tx = tx;
    }

    public PlatformTransactionManager transactionManager() {
        return tx;
    }

    @Override
    public UserTransaction<T> createTransaction(TransactionDescriptor descriptor) {
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        if (descriptor.isolation() != null) {
            def.setIsolationLevel(descriptor.isolation().jdbcCode());
        }
        switch (descriptor.requirement()) {
            case ALWAYS_NEW:
            case ALWAYS_NEW_LINKED:
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                break;
            case NESTED:
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);
                break;
            case PROPAGATED_ONLY:
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_MANDATORY);
                break;
            case NEW_OR_PROPAGATED:
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        }
        TransactionStatus status = tx.getTransaction(def);

        return new SpringUserTransaction<>(newEngine(), tx, status);
    }

    public abstract T newEngine();

}
