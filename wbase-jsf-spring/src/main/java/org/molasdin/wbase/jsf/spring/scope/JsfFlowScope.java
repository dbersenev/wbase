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

package org.molasdin.wbase.jsf.spring.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import javax.faces.context.FacesContext;
import javax.faces.flow.FlowHandler;
import java.util.Map;


public class JsfFlowScope implements Scope {
    @Override
    public Object get(String s, ObjectFactory<?> objectFactory) {
        Map<Object, Object> scope = flowScope();
        if(scope == null){
            return null;
        }

        if(scope.containsKey(s)){
            return scope.get(s);
        }

        Object o = objectFactory.getObject();
        scope.put(s, o);
        return o;
    }

    @Override
    public Object remove(String s) {
        Map<Object, Object> scope = flowScope();
        if(scope == null){
            return null;
        }
        return scope.remove(s);
    }

    @Override
    public void registerDestructionCallback(String s, Runnable runnable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object resolveContextualObject(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getConversationId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private Map<Object,Object> flowScope(){
        if(FacesContext.getCurrentInstance() == null){
            return null;
        }
        FlowHandler handler = FacesContext.getCurrentInstance().getApplication().getFlowHandler();
        if(handler == null){
            return null;
        }
        return handler.getCurrentFlowScope();
    }
}
