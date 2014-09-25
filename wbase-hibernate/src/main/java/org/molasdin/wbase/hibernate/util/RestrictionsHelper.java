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

package org.molasdin.wbase.hibernate.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 * Created by dbersenev on 21.01.14.
 */
public class RestrictionsHelper {

    private final static String ILIKE_HQL = "upper(%s) like upper('%s')";

    private static Pair<String, MatchMode> wildToNormal(String value, MatchMode mode){
        boolean starts = value.startsWith("*");
        boolean ends = value.endsWith("*") && !value.endsWith("\\*");
        value = StringUtils.removeStart(value, "*").replaceFirst("^\\*", "*");
        value = ends?StringUtils.removeEnd(value, "*"):value.replaceFirst("\\\\[*]$", "*");
        if(starts && ends){
            mode = MatchMode.ANYWHERE;
        }else if(starts){
            mode = MatchMode.END;
        }else if(ends){
            mode = MatchMode.START;
        }
        return Pair.of(value, mode);
    }

    public static Criterion ilikeWild(String prop, String value, MatchMode mode){
        Pair<String, MatchMode> result = wildToNormal(value, mode);
        return Restrictions.ilike(prop, result.getLeft(), result.getRight());
    }

    public static String ilikeWildHql(String prop, String value, MatchMode mode){
        Pair<String, MatchMode> result = wildToNormal(value, mode);
        mode = result.getRight();
        value = result.getLeft();
        String format = null;
        if(MatchMode.START.equals(mode)){
            format = "%s%%";
        } else if(MatchMode.END.equals(mode)){
            format = "%%%s";
        } else if (MatchMode.ANYWHERE.equals(mode)){
            format = "%%%s%%";
        } else{
            format = "%s";
        }

        return String.format(ILIKE_HQL, prop, String.format(format, value));
    }
}
