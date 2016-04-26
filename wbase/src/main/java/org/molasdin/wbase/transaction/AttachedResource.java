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

package org.molasdin.wbase.transaction;

import org.molasdin.wbase.Resource;

/**
 * Created by dbersenev on 21.04.2016.
 */

/**
 * Used to export some resource produced during transaction
 * When closed transaction will be closed as well
 * @param <T>
 */
public class AttachedResource<T extends AutoCloseable> implements Resource<T> {
    private T res;
    private Transaction tx;

    public AttachedResource(T res, Transaction tx) {
        this.res = res;
        this.tx = tx;
    }

    @Override
    public T resource() {
        return res;
    }

    @Override
    public void close() {
        try {
            res.close();
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            tx.close();
        }
    }
}
