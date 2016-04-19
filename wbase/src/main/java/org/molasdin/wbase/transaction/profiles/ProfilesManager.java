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

package org.molasdin.wbase.transaction.profiles;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by dbersenev on 10.12.2014.
 */
public enum ProfilesManager {
    INSTANCE;
    private Map<String, TransactionProfile> profiles = new HashMap<String, TransactionProfile>();
    private CommonTransactionProfile commonTransactionProfile = new CommonTransactionProfile();

    {
        profiles.put("oracle", new OracleTransactionProfile());
    }
    public TransactionProfile profileFor(final String dbName){
        Optional<String> key = profiles.keySet().stream().filter(e -> StringUtils.containsIgnoreCase(dbName, e)).findFirst();
        if(!key.isPresent()){
            return commonTransactionProfile;
        }

        return profiles.get(key.get());
    }
}
