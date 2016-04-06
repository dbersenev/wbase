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

package org.molasdin.wbase.registry;

import org.molasdin.wbase.storage.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dbersenev on 03.02.14.
 */
public class SimpleRegistry implements Registry{

    private final Map<String, Object> objects = new HashMap<String, Object>();

    @Override
    public void attach(String name, Object item) {
        objects.put(name, item);
    }

    @Override
    public <T> T item(String name, Class<T> clazz) {
        if(!objects.containsKey(name)){
            return null;
        }
        return clazz.cast(objects.get(name));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, K extends Serializable> Repository<T, K> repository(String name) {
        return (Repository<T, K>)item(name, Repository.class);
    }


}
