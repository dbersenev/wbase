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

package org.molasdin.wbase.hibernate;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.molasdin.wbase.transaction.manager.Engine;

import java.util.List;
import java.util.Map;

/**
 * Created by dbersenev on 28.10.2014.
 */
public interface HibernateEngine extends Engine {
    Session session();
    <U> List<U> queryForList(String query, Map<String, ?> arguments, Class<U> clazz);
    <U> List<U> queryForList(Query query, Class<U> clazz);

    <U> List<U> invokeCriteriaForList(DetachedCriteria criteria, Class<U> clazz);
    <U> List<U> invokeCriteriaForList(Criteria criteria, Class<U> clazz);

    <U> U queryForSingle(String query, Map<String, ?> arguments, Class<U> clazz);
    <U> U queryForSingle(Query query, Class<U> clazz);

    <U> U invokeCriteriaForSingle(DetachedCriteria criteria, Class<U> clazz);
    <U> U invokeCriteriaForSingle(Criteria criteria, Class<U> clazz);
}
