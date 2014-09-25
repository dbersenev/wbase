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

import org.apache.commons.lang3.text.StrBuilder;
import org.hibernate.criterion.MatchMode;
import org.molasdin.wbase.hibernate.util.RestrictionsHelper;

/**
 * Created by dbersenev on 24.02.14.
 */
public class BasicQueryBuilder implements QueryBuilder {
    private final static String FROM = "from %s as %s";
    private final static String INNER_JOIN = "inner join %s as %s";
    private final static String OP = "%s %s %s";

    private boolean hasCondition = false;

    private StrBuilder builder = new StrBuilder();

    @Override
    public QueryBuilder addPart(String value) {
        builder.appendSeparator(' ');
        if(hasCondition){
            builder.appendSeparator(" AND ");
        }
        builder.append(value);
        return this;
    }

    @Override
    public QueryBuilder addFrom(String name, String alias) {
        return addPart(String.format(FROM, name, alias));
    }

    @Override
    public QueryBuilder addInnerJoin(String prop, String alias) {
        return addPart(String.format(INNER_JOIN, prop, alias));
    }

    @Override
    public QueryBuilder addWhere(BuilderScope scope, boolean isEnabled) {
        if(!isEnabled){
            return this;
        }
        addPart("where");
        scope.append(this);
        hasCondition = false;
        return this;
    }

    @Override
    public QueryBuilder ilikeWild(String prop, String value, MatchMode mode) {
        addPart(RestrictionsHelper.ilikeWildHql(prop, value, mode));
        hasCondition = true;
        return this;
    }

    @Override
    public QueryBuilder gt(String prop, String param) {
        return addOp(prop, param, ">");
    }

    @Override
    public QueryBuilder lt(String prop, String param) {
        return addOp(prop, param, "<");
    }

    public QueryBuilder addOp(String prop, String param, String op){
        addPart(String.format(OP, prop, param, op));
        hasCondition = true;
        return this;
    }

    @Override
    public String query() {
        return builder.toString();
    }
}
