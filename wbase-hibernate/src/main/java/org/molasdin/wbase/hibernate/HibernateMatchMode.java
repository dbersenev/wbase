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

import org.molasdin.wbase.storage.FilteringMode;

/**
 * Created by dbersenev on 11.03.14.
 */
public enum HibernateMatchMode {
    START("%s%%", FilteringMode.START),
    END("%%%s", FilteringMode.END),
    MIDDLE("%%%s%%", FilteringMode.MIDDLE);


    private String template;
    private FilteringMode mode;

    private HibernateMatchMode(String template, FilteringMode mode) {
        this.template = template;
        this.mode = mode;
    }

    public String template(){
        return template;
    }

    public FilteringMode mode(){
        return mode;
    }

    public static HibernateMatchMode fromFilteringMode(FilteringMode mode){
        for(HibernateMatchMode matchMode: HibernateMatchMode.values()){
            if(matchMode.mode().equals(mode)){
                return matchMode;
            }
        }
        return null;
    }
}
