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

package org.molasdin.wbase;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.util.*;


public class ReflectionHelper {
    /**
     * Extracts all properties from the bean
     * @param bean
     * @return
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> extractAll(Object bean){
        Map<String, Object> description = Collections.emptyMap();
        try{
            description = PropertyUtils.describe(bean);
            description.remove("class");
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return description;
    }


    /**
     * Extracts only not null properties
     * @param bean
     * @return
     */
    public static Map<String, Object> extractNotNullProperties(Object bean){
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> descr = extractAll(bean);
        for(String prop: descr.keySet()){
            if(descr.get(prop) != null){
                result.put(prop, descr.get(prop));
            }
        }
        return result;
    }

    /**
     * Determines if all properties are null and collections are empty
     * @param bean
     * @return
     */
    public static boolean isEmpty(Object bean){
        Map<String, Object> descr = extractAll(bean);
        for(String prop: descr.keySet()){
            Object o = descr.get(prop);
            if(o != null){
                if(o instanceof Collection){
                    if(((Collection) o).isEmpty()){
                       continue;
                    }
                }
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static boolean hasFunction(String name, Object object){
        Class clazz = object.getClass();
        try{
            clazz.getDeclaredMethod(name);
        } catch (Exception ex){
            return false;
        }
        return true;
    }

    public static Object functionValue(String name, Object object){
        try{
            return MethodUtils.invokeExactMethod(object, name, new Object[0]);
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static boolean supportsNaturalOrdering(Object object){
        return object instanceof Comparable;
    }

    public static boolean isNumeric(Object value){
        return value instanceof Number;
    }
}
