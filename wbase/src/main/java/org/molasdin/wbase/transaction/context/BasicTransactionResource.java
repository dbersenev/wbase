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

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dbersenev on 14.04.2016.
 */
public class BasicTransactionResource<T> implements TransactionResource<T> {
    private T resource;
    private boolean stable;
    private ResourceClosure<T> onClose;

    private Set<ExtendedTransaction> clients = new HashSet<>();

    public BasicTransactionResource(T resource, boolean stable, ResourceClosure<T> onClose) {
        this.resource = resource;
        this.stable = stable;
        this.onClose = onClose;
    }

    @Override
    public boolean isStable() {
        return stable;
    }

    @Override
    public T resource() {
        return resource;
    }

    @Override
    public Set<ExtendedTransaction> clients() {
        return clients;
    }

    @Override
    public void close() {
        if(onClose != null) {
            try {
                onClose.close(resource);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
