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

/**
 * Created by dbersenev on 30.03.2016.
 */
public class AbstractTransaction implements Transaction {
    private boolean wasRolledBack = false;
    private boolean wasCommitted = false;

    @Override
    public void rollback() {
        wasRolledBack = true;
    }

    @Override
    public void commit() {
        setRolledBack(false);
    }

    @Override
    public boolean wasRolledBack() {
        return wasRolledBack;
    }
    public void setRolledBack(boolean flag){
        wasRolledBack = flag;
    }

    @Override
    public boolean wasCommitted() {
        return wasCommitted;
    }
    public void setCommitted(boolean wasCommitted) {
        this.wasCommitted = wasCommitted;
    }
}
