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

package org.molasdin.wbase.batis;

import org.molasdin.wbase.registry.Registry;
import org.molasdin.wbase.registry.RegistryManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by dbersenev on 17.02.14.
 */
public class SqlSessionFactoryListener implements ServletContextListener {

    private final static String CONFIG_LOCATION = "wbase.batis.CONFIG_LOCATION";
    private final static String NO_CONFIG = "CONFIG_LOCATION parameter is not specified";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String location = servletContextEvent.getServletContext().getInitParameter(CONFIG_LOCATION);
        if(location == null){
            throw new RuntimeException(NO_CONFIG);
        }
        Registry registry = RegistryManager.INSTANCE.currentRegistry();
        BatisUtil.initializeBatis(location, registry);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
