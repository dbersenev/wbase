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

package org.molasdin.wbase.storage;

import java.util.HashMap;
import java.util.Map;


public abstract class AbstractSearchConfiguration<T> implements SearchConfiguration<T> {

    private Map<String, FilteringMode> matchModes = new HashMap<String, FilteringMode>();

    public String baseProperty() {
        return null;
    }

    public String distinctProperty() {
        return null;
    }

    @Override
    public String propertiesPrefix() {
        return null;
    }

    public Map<String, FilteringMode> filterModes() {
        return matchModes;
    }
}
