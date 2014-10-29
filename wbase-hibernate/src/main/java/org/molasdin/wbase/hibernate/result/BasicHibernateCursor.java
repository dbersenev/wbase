/*
 * Copyright 2013 Bersenev Dmitry molasdin@outlook.com
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

package org.molasdin.wbase.hibernate.result;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;


import org.hibernate.criterion.*;
import org.molasdin.wbase.hibernate.HibernateSupport;
import org.molasdin.wbase.hibernate.criterion.FilterCriterion;
import org.molasdin.wbase.storage.*;

import java.util.List;
import java.util.Map;


public class BasicHibernateCursor<T> extends CommonHibernateCursor<T, DetachedCriteria> {

    public BasicHibernateCursor() {
    }

    public BasicHibernateCursor(HibernateSupport support) {
        super(support);
    }

    public static MatchMode toMatchMode(FilteringMode mode){
        switch (mode){
            case START:
                return MatchMode.START;
            case END:
                return MatchMode.END;
            case MIDDLE:
                return MatchMode.ANYWHERE;
            default:
                return MatchMode.EXACT;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> dataCallback(Session session) {
        DetachedCriteria criteria = searchSpecification().query();
        List<Pair<String, org.molasdin.wbase.storage.Order>> orders = orders();
        if(!orders.isEmpty()){
            for(Pair<String, org.molasdin.wbase.storage.Order> order: orders){
                String prop = translateProperty(order.getLeft());
                criteria.addOrder(org.molasdin.wbase.storage.Order.ASC.equals(order.getRight()) ? org.hibernate.criterion.Order.asc(prop) :
                        org.hibernate.criterion.Order.desc(prop));
            }
        }

        criteria.add(populateFilters(searchSpecification().filterModes()));
        return postProcessData((List<T>)criteria.getExecutableCriteria(session)
                .setFirstResult(calculatedRowOffset())
                .setMaxResults(pageSize())
                .list());
    }

    @Override
    public Long totalCallback(Session session) {
        DetachedCriteria criteria = searchSpecification().query();
        if(searchSpecification().distinctProperty() != null){
            criteria.setProjection(Projections.countDistinct(searchSpecification().distinctProperty()));
        }else{
            criteria.setProjection(Projections.rowCount());
        }
        criteria.add(populateFilters(searchSpecification().filterModes()));
        return (Long)criteria.getExecutableCriteria(session).uniqueResult();
    }

    private Conjunction populateFilters(Map<String, FilteringMode> matchModes){
        Conjunction filterCriterion = Restrictions.conjunction();
        if(filters().size() > 0){
            for(String prop: filters().keySet()){
                String translated = translateProperty(prop);

                MatchMode mode = matchModes.containsKey(prop)?toMatchMode(matchModes.get(prop)):MatchMode.START;
                filterCriterion.add(new FilterCriterion(translated, filters().get(prop).getRight(), mode));
//                filterCriterion.add(Restrictions.ilike(translated, filters().get(prop).getRight(), MatchMode.START));
            }
        }
        return filterCriterion;
    }


    @Override
    public Cursor<T> copy() {
        BasicHibernateCursor<T> newSearchResult = new BasicHibernateCursor<T>();
        newSearchResult.setSearchConfiguration(searchSpecification());
        newSearchResult.setHibernateSupport(support());
        return newSearchResult;
    }
}
