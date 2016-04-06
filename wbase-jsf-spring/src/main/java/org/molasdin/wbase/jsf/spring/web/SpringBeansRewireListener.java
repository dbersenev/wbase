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

package org.molasdin.wbase.jsf.spring.web;

import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

/**
 * Created by dbersenev on 08.12.2014.
 */
public class SpringBeansRewireListener implements HttpSessionActivationListener {
    @Override
    public void sessionWillPassivate(HttpSessionEvent httpSessionEvent) {

    }

    @Override
    public void sessionDidActivate(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(session.getServletContext());
        AutowireCapableBeanFactory bf = ctx.getAutowireCapableBeanFactory();
        for(String entry: IteratorUtils.asIterable(IteratorUtils.asIterator(session.getAttributeNames()))){
            if(bf.containsBean(entry)){
                Object bean = session.getAttribute(entry);
                bean = bf.configureBean(bean, entry);
                session.setAttribute(entry, bean);
            }
        }

    }
}
