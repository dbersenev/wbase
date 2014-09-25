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

package org.molasdin.wbase.hibernate.search;

import org.hibernate.criterion.MatchMode;

/**
 * Created by dbersenev on 24.02.14.
 */
public interface QueryBuilder {
    QueryBuilder addPart(String value);
    QueryBuilder addFrom(String name, String alias);
    QueryBuilder addInnerJoin(String prop, String alias);
    QueryBuilder addWhere(BuilderScope scope, boolean isEnabled);

    QueryBuilder ilikeWild(String prop, String value, MatchMode mode);
    QueryBuilder gt(String prop, String param);
    QueryBuilder lt(String prop, String param);

    String query();
}
