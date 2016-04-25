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

package org.molasdin.wbase.transaction.context;

import java.util.Set;
import java.util.function.Function;

/**
 * Created by dbersenev on 21.04.2016.
 */
public class TransactionProxyResource<T> implements TransactionResource<T> {

    private TransactionResource<T> original;
    private T proxy;
    private Function<T,T> proxyMaker;

    public TransactionProxyResource(TransactionResource<T> original, Function<T,T> proxyMaker) {
        this.original = original;
        this.proxyMaker = proxyMaker;
    }

    @Override
    public T resource() {
        if(proxy == null) {
            proxy = proxyMaker.apply(original.resource());
        }
        return proxy;
    }

    @Override
    public Set<ExtendedTransaction> clients() {
        return original.clients();
    }

    @Override
    public boolean isStable() {
        return original.isStable();
    }

    @Override
    public void close() {
        original.close();
    }
}
