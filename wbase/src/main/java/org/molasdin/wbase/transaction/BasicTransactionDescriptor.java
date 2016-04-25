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

package org.molasdin.wbase.transaction;

/**
 * Created by dbersenev on 28.10.2014.
 */
public class BasicTransactionDescriptor implements TransactionDescriptor{
    private TransactionIsolation isolation;
    private Requirement requirement = Requirement.NEW_OR_PROPAGATED;

    public BasicTransactionDescriptor() {
    }

    public BasicTransactionDescriptor(TransactionIsolation isolation) {
        this.isolation = isolation;
    }

    public void setIsolation(TransactionIsolation isolation) {
        this.isolation = isolation;
    }

    @Override
    public TransactionIsolation isolation() {
        return isolation;
    }

    @Override
    public Requirement requirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    public void merge(TransactionDescriptor descr) {
        if(descr.isolation() != null){
            setIsolation(descr.isolation());
        }

        setRequirement(descr.requirement());
    }
}
