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

package org.molasdin.wbase.batis.search;

import org.molasdin.wbase.storage.AbstractSearchConfiguration;
import org.molasdin.wbase.storage.SearchConfiguration;

import java.util.Map;

/**
 * Created by dbersenev on 18.03.14.
 */
public class Search {
    public static <T> SearchConfiguration<T,Map<String,Object>> fromMap(final Map<String,Object> parameters){
        return new AbstractSearchConfiguration<T, Map<String, Object>>() {
            @Override
            public Map<String, Object> query() {
                return parameters;
            }
        };
    }
}
