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

package org.molasdin.wbase.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.engine.spi.TypedValue;


public class FilterCriterion implements Criterion {

    private final static int FILTER_MAX_CHARS = 100;
//    private final static String FILTER_PART2 = "upper(cast(%s as char(%d))) like upper('%s%%')";
    private final static String FILTER_PART2 = "upper(cast(%s as char(%d))) like upper('%s')";

    private final static String MATCH_START = "%s%%";
    private final static String MATCH_END = "%%%s";
    private final static String MATCH_ANYWHERE = "%%%s%%";

    private String property;
    private Object value;
    private String matchFormat = MATCH_START;

    public FilterCriterion(String property, Object value) {
        this(property, value, MatchMode.START);
    }

    public FilterCriterion(String property, Object value, MatchMode mode) {
        this.property = property;
        this.value = value;
        switch (mode){
            case START:
                matchFormat = MATCH_START;
                break;
            case END:
                matchFormat = MATCH_END;
                break;
            case ANYWHERE:
                matchFormat = MATCH_ANYWHERE;
                break;
            default:
                matchFormat = MATCH_START;
        }
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
//        Dialect dialect = criteriaQuery.getFactory().getDialect();
        String[] columns = criteriaQuery.findColumns( property, criteria);
        if ( columns.length != 1 ) {
            throw new HibernateException( "Filter may only be used with single-column properties" );
        }

        String propFormatted = String.format(matchFormat, value.toString());
        return String.format(FILTER_PART2, columns[0], FILTER_MAX_CHARS, propFormatted);
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return new TypedValue[0];
    }

    @Override
    public String toString() {
        return String.format("%s filtered by %s", property, value.toString());
    }
}
