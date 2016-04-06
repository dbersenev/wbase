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

package org.molasdin.wbase.hibernate.search;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by dbersenev on 11.03.2016.
 */
public class Search {
    public static CursorCriteria all(Class<?> clazz){
        return new CursorCriteria() {
            @Override
            public DetachedCriteria newSearchCriteria() {
                return DetachedCriteria.forClass(clazz);
            }

            @Override
            public DetachedCriteria newCountCriteria() {
                return DetachedCriteria.forClass(clazz).setProjection(Projections.distinct(Projections.rowCount()));
            }
        };
    }

    public static Function<Session, Query>  filterAllQuery(Collection<?> c){
        return (s) -> s.createFilter(c, "");
    }

    public static Function<Session, Query> filterAllCount(Collection<?> c) {
        return (s)-> s.createFilter(c, "select count(*) ");
    }
}
