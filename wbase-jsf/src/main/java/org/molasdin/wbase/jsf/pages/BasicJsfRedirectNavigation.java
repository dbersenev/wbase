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

package org.molasdin.wbase.jsf.pages;

import org.apache.commons.lang3.text.StrBuilder;
import org.omnifaces.util.Faces;

/**
 * Created by dbersenev on 18.06.2014.
 */
public class BasicJsfRedirectNavigation implements Navigation {

    private StrBuilder builder = new StrBuilder();

    private Boolean hasParameters = false;

    public BasicJsfRedirectNavigation(String url) {
        builder.append(Faces.getExternalContext().getApplicationContextPath().concat(url));
    }

    @Override
    public Navigation addParam(String name, String value){
        p();
        builder.append(name).append("=").append(value);
        return this;
    }

    @Override
    public Navigation addRawParam(String name, Object value) {
        p();
        builder.append(name).append("=").append(value.toString());
        return this;
    }

    @Override
    public void invoke() {
        try{
            Faces.redirect(builder.toString());
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private void p(){
        if(!hasParameters){
            builder.append('?');
        } else{
            builder.appendSeparator('&');
        }
        hasParameters = true;
    }


}
