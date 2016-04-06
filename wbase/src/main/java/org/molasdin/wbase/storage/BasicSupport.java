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

package org.molasdin.wbase.storage;

import org.molasdin.wbase.transaction.manager.Engine;
import org.molasdin.wbase.transaction.manager.TransactionManager;

/**
 * Created by dbersenev on 15.10.2014.
 */
public class BasicSupport<T extends Engine> implements Support<T> {
    private TransactionManager<T> provider;

    public void setDefaultTransactionProvider(TransactionManager<T> provider) {
        this.provider = provider;
    }

    public TransactionManager<T> defaultTransactionProvider() {
        if(provider != null){
            return provider;
        }

        synchronized (this){
            if(provider != null) {
                return provider;
            }
            TransactionManager<T> newOne = newDefaultProvider();
            if(newOne != null){
                provider = newOne;
            }
        }
        return provider;
    }

    protected TransactionManager<T> newDefaultProvider(){
        return null;
    }


}
