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

package org.molasdin.wbase.jsf.spring.messages;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceResourceBundle;

import javax.faces.context.FacesContext;
import java.util.Locale;
import java.util.ResourceBundle;


public class JSFMessagesFactoryBean implements MessageSourceAware, FactoryBean<ResourceBundle> {

    private MessageSource source;

    public void setMessageSource(MessageSource messageSource) {
        this.source = messageSource;
    }

    public ResourceBundle getObject() throws Exception {
        //TODO: investigate issue with the early access
        FacesContext context = FacesContext.getCurrentInstance();
        Locale locale = context.getViewRoot().getLocale();
        return new MessageSourceResourceBundle(source, locale);
    }

    public Class<?> getObjectType() {
        return ResourceBundle.class;
    }

    public boolean isSingleton() {
        return true;
    }

}
