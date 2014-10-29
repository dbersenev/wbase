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

package org.molasdin.wbase.batis.support;

import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.SqlSession;

/**
 * Created by dbersenev on 31.01.14.
 */
public class BasicBatisEngine<M> implements BatisEngine<M> {

    private SqlSession session;
    private M mapper;

    public BasicBatisEngine(SqlSession session, M mapper) {
        this.session = session;
        this.mapper = mapper;
    }

    @Override
    public SqlSession session() {
        return session;
    }

    @Override
    public M mapper() {
        return mapper;
    }

    @Override
    public String columnByProperty(String property, String resultMap) {
        ResultMap map = null;
        map = session.getConfiguration().getResultMap(resultMap);
        if (property.contains(".")){
            String[] parts = property.split("\\.");
            property = parts[parts.length - 1];
            for(int i = 0; i < parts.length - 1;i++){
                resultMap = resultMapNameFromProp(parts[i], map);
                if(resultMap == null){
                    throw new IllegalArgumentException(String.format("Can not find result map for property: %s", parts[i]));
                }
                map = session.getConfiguration().getResultMap(resultMap);
            }
        } else{
            map = session.getConfiguration().getResultMap(resultMap);
        }

        if(property.startsWith("$")){
            Integer number = Integer.parseInt(property.replace("$", ""));
            ResultMapping mapping = map.getConstructorResultMappings().get(number);
            return mapping.getColumn();
        }
        for(ResultMapping mapping: map.getPropertyResultMappings()){
            if(mapping.getProperty().equals(property)){
                return mapping.getColumn();
            }
        }
        throw new IllegalArgumentException(String.format("Can not find property: %s", property));
    }

    private String resultMapNameFromProp(String prop, ResultMap resultMap){
        for(ResultMapping mapping: resultMap.getResultMappings()){
            if(mapping.getNestedResultMapId() != null &&
                    prop.equals(mapping.getProperty())){
                return mapping.getNestedResultMapId();
            }
        }
        return null;
    }

    public static<F> BatisEngine<F> of(SqlSession session, F mapper){
        return new BasicBatisEngine<F>(session, mapper);
    }
}
