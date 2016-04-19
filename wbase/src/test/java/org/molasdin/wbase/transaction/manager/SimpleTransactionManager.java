/*
 * Copyright 2016 Bersenev Dmitry molasdin@outlook.com
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

package org.molasdin.wbase.transaction.manager;

import org.molasdin.wbase.transaction.context.config.UserTransactionConfiguration;
import org.molasdin.wbase.transaction.context.interceptors.InterceptionMode;

/**
 * Created by dbersenev on 19.04.2016.
 */
public class SimpleTransactionManager extends AbstractTransactionManager<SimpleEngine> {

    private final static String RESOURCE_KEY = "KEY";


    @Override
    protected void configure(UserTransactionConfiguration<SimpleEngine> cfg) throws Exception {

        TestResource resource = null;

        if(cfg.descriptor().requirement().hasNewSemantics() || !cfg.hasResource(RESOURCE_KEY)) {
            cfg.bindResource(RESOURCE_KEY, new TestResource(), (r) -> System.out.println(r.toString().concat(" closed")));
        }
        resource = cfg.resource(RESOURCE_KEY);
        cfg.setInterceptionMode(InterceptionMode.ALL);
        cfg.setUnderline(new SimpleEngine(resource), new SimpleTransaction());
        cfg.interception().addStart((e) -> System.out.println("Start"));
        cfg.interception().addPreCommit((e)->System.out.println("Pre commit"));
        cfg.interception().addPostCommit((e)->System.out.println("Post commit"));
        cfg.interception().addPreRollback((e)->System.out.println("Pre rollback"));
        cfg.interception().addPostRollback((e)->System.out.println("Post rollback"));
        cfg.interception().addPreClose((e)->System.out.println("Pre close"));
        cfg.interception().addPostClose((e)->System.out.println("Post close"));
    }
}
