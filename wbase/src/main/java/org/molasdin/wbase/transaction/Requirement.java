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
 * Created by molasdin on 1/30/16.
 */
public enum Requirement {
    ALWAYS_NEW {
        @Override
        public boolean hasNewSemantics() {
            return true;
        }
    },
    ALWAYS_NEW_LINKED {
        @Override
        public boolean hasNewSemantics() {
            return true;
        }
    },
    NESTED {
        @Override
        public boolean hasNewSemantics() {
            return true;
        }
    },
    NEW_OR_PROPAGATED,
    PROPAGATED_ONLY;


    public boolean hasNewSemantics() {
        return false;
    }
}
