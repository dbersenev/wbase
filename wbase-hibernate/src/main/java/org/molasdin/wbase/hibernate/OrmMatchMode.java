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

import org.hibernate.criterion.MatchMode;
import org.molasdin.wbase.storage.FilteringMode;

import java.util.logging.Filter;

/**
 * Created by dbersenev on 11.03.14.
 */
public enum OrmMatchMode {
    START("%s%%", FilteringMode.START),
    END("%%%s", FilteringMode.END),
    MIDDLE("%%%s%%", FilteringMode.MIDDLE);


    private String template;
    private FilteringMode mode;

    private OrmMatchMode(String template, FilteringMode mode) {
        this.template = template;
        this.mode = mode;
    }

    public String template(){
        return template;
    }

    public FilteringMode mode(){
        return mode;
    }

    public static OrmMatchMode fromFilteringMode(FilteringMode mode){
        for(OrmMatchMode matchMode: OrmMatchMode.values()){
            if(matchMode.mode().equals(mode)){
                return matchMode;
            }
        }
        return null;
    }
}
