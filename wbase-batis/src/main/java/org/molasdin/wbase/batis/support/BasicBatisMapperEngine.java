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

import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.SqlSession;
import org.molasdin.wbase.batis.cursor.Restriction;
import org.molasdin.wbase.batis.cursor.SimpleRestriction;
import org.molasdin.wbase.storage.Order;
import org.molasdin.wbase.storage.FilterAndOrder;

import java.util.*;

/**
 * Created by dbersenev on 31.01.14.
 */
public class BasicBatisMapperEngine implements BatisMapperEngine {

    private SqlSession session;

    public BasicBatisMapperEngine(SqlSession session) {
        this.session = session;
    }

    @Override
    public SqlSession session() {
        return session;
    }

    @Override
    public <M> M mapper(Class<M> clazz) {
        return session.getMapper(clazz);
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

    @Override
    public Restriction fromDetails(FilterAndOrder s, String mappingName) {
        return new SimpleRestriction(columnOrders(s, mappingName), columnFilters(s, mappingName));
    }

    @Override
    public Restriction fromDetails(long start, FilterAndOrder s, String mappingName) {
        return new SimpleRestriction(start, columnOrders(s, mappingName), columnFilters(s, mappingName));
    }

    @Override
    public Restriction fromDetails(long offset, long size, FilterAndOrder s, String mappingName) {
        return new SimpleRestriction(offset,size,columnOrders(s, mappingName), columnFilters(s, mappingName));
    }

    private Map<String,String> columnFilters(FilterAndOrder s, String mapperName){
        Map<String,String> result = new HashMap<>();
        for(String entry: s.filters().keySet()){
            String value = s.filters().get(entry).getRight();
            result.put(columnByProperty(entry, mapperName), value);
        }
        return result;
    }

    private List<Pair<String,Order>> columnOrders(FilterAndOrder s, String mapperName){
        List<Pair<String, Order>> oldOrder = s.orders();
        if(oldOrder == null){
            return null;
        }
        List<Pair<String,Order>> result = new ArrayList<>(s.orders().size());
        for(Pair<String, Order> entry: oldOrder){
            if(entry.getLeft() == null || entry.getRight() == null){
                return null;
            }
            result.add(Pair.of(columnByProperty(entry.getLeft(), mapperName), entry.getRight()));
        }
        return result;
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

    public static BatisMapperEngine of(SqlSession session){
        return new BasicBatisMapperEngine(session);
    }

   }
