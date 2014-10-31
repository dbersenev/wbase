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

package org.molasdin.wbase.hibernate.result;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Query;
import org.hibernate.Session;
import org.molasdin.wbase.hibernate.HibernateEngine;
import org.molasdin.wbase.hibernate.HibernateMatchMode;
import org.molasdin.wbase.hibernate.HibernateSupport;
import org.molasdin.wbase.storage.FilteringMode;
import org.molasdin.wbase.storage.Order;
import org.molasdin.wbase.transaction.TransactionRunner;

import java.util.List;
import java.util.Map;

public class BasicHibernateQueryCursor<T> extends CommonHibernateCursor<T, Pair<Pair<String,String>, Map<String,Object>>> {

    private final static String ORDER_BY = "order by ";
    private final static String ORDER_BY_CLAUSE = "%s %s";
    private final static String FILTER_PART = "upper(str(%s)) like upper('%s')";

    public BasicHibernateQueryCursor() {
    }

    public BasicHibernateQueryCursor(TransactionRunner<HibernateEngine> runner) {
        super(runner);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> dataCallback(Session session) {
        Pair<Pair<String,String>, Map<String,Object>> spec = searchSpecification().query();
        Pair<String,String> query = spec.getLeft();
        StrBuilder resultQuery = new StrBuilder();
        resultQuery.append(query.getLeft());
        resultQuery.append(' ');
        resultQuery.append(query.getRight());
        populateFilters(resultQuery, spec.getLeft().getRight(), searchSpecification().filterModes());
        List<Pair<String, Order>> orders = orders();
        if(!orders.isEmpty()){
            StrBuilder builder = new StrBuilder(ORDER_BY);
            int pos = 0;
            for(Pair<String, Order> order: orders){
                String prop = translateProperty(order.getLeft());
                builder.appendSeparator(",", pos);
                builder.append(String.format(ORDER_BY_CLAUSE, prop, Order.ASC.equals(order.getRight()) ? "asc" : "desc"));
                pos++;
            }
            resultQuery.append(' ').append(builder.toString());
        }

        int rowOffset = calculatedRowOffset();

        Query q = session.createQuery(resultQuery.toString());
        for(String param: spec.getRight().keySet()){
            q.setParameter(param, spec.getRight().get(param));
        }
        return  postProcessData((List<T>)q
                .setFirstResult(rowOffset)
                .setMaxResults(pageSize())
                .list());
    }

    @Override
    public Long totalCallback(Session session) {
        Pair<Pair<String,String>, Map<String,Object>> spec = searchSpecification().query();
        StrBuilder query = new StrBuilder("select ");
        if(searchSpecification().distinctProperty() != null){
            query.append("count( distinct ").append(searchSpecification().distinctProperty()).append(" )");
        }else{
            query.append("count(*)");
        }
        query.append(' ');
        query.append(spec.getLeft().getRight());
        populateFilters(query, spec.getLeft().getRight(), searchSpecification().filterModes());
        Query q = session.createQuery(query.toString());
        for(String param: spec.getRight().keySet()){
            q.setParameter(param, spec.getRight().get(param));
        }
        return (Long)q.uniqueResult();
    }

    private void populateFilters(StrBuilder builder, String from, Map<String, FilteringMode> matchModes){
        if(filters().size() > 0){
            builder.append(' ');
            if(!StringUtils.containsIgnoreCase(from, "where")){
                builder.append("where").append(" (");
            } else{
                builder.append(" and (");
            }
            int pos = 0;
            for(String prop: filters().keySet()){
                String translated = translateProperty(prop);
                HibernateMatchMode matchMode = HibernateMatchMode.START;
                if(matchModes.containsKey(prop)){
                    matchMode = HibernateMatchMode.fromFilteringMode(matchModes.get(prop));
                }
                String template = matchMode.template();
                builder.appendSeparator(" and ", pos);
                builder.append(String.format(FILTER_PART, translated, String.format(template, filters().get(prop).getRight())));
                pos++;
            }
            builder.append(")");
        }
    }
}
