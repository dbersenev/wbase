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

import org.molasdin.wbase.transaction.Engine;
import org.molasdin.wbase.transaction.EngineFactory;
import org.molasdin.wbase.transaction.TransactionProvider;
import org.molasdin.wbase.transaction.TransactionProviderFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Created by dbersenev on 28.10.2014.
 */
public class SpringTransactionProviderFactory<T extends Engine> implements TransactionProviderFactory<T> {
    private PlatformTransactionManager tx;
    private EngineFactory<T> engineFactory;

    public SpringTransactionProviderFactory(PlatformTransactionManager tx, EngineFactory<T> engineFactory) {
        this.tx = tx;
        this.engineFactory = engineFactory;
    }

    @Override
    public TransactionProvider<T> createProvider() {
        return new SpringTransactionProvider<T>(tx, engineFactory);
    }
}
