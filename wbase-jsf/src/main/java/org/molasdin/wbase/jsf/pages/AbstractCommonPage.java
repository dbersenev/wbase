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

import org.apache.commons.lang3.StringUtils;
import org.molasdin.wbase.jsf.messages.MessageTools;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;

import javax.faces.application.FacesMessage;

/**
 * Created by dbersenev on 18.06.2014.
 */
public class AbstractCommonPage implements CommonPage {

    private Boolean showMessagesInDialog = false;

    public Boolean isShowMessagesInDialog() {
        return showMessagesInDialog;
    }

    public void setShowMessagesInDialog(Boolean showMessagesInDialog) {
        this.showMessagesInDialog = showMessagesInDialog;
    }

    @Override
    public String messageBoxId() {
        return MessageTools.MESSAGE_BOX_ID;
    }

    @Override
    public void showError(String shortMessage, String details, Object... args) {
        showMessage(FacesMessage.SEVERITY_FATAL, shortMessage, details, args);
    }

    @Override
    public void showWarning(String shortMessage, String details, Object... args) {
        showMessage(FacesMessage.SEVERITY_WARN, shortMessage, details, args);
    }

    @Override
    public void showInfo(String shortMessage, String details, Object... args) {
        showMessage(FacesMessage.SEVERITY_INFO, shortMessage, details, args);
    }

    private void showMessage(FacesMessage.Severity severity, String shortPart, String details, Object... args){
        FacesMessage message = Messages.create(severity, shortPart, args);
        if(StringUtils.isNotBlank(details)){
            message.setDetail(details);
        }

        if(isShowMessagesInDialog()){
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        } else{
            Messages.add(messageBoxId(), message);
        }
    }

    @Override
    public void showErrorSimple(String message, Object... args) {
        showMessage(FacesMessage.SEVERITY_FATAL, message, null, args);
    }

    @Override
    public void showWarningSimple(String message, Object... args) {
        showMessage(FacesMessage.SEVERITY_WARN, message, null, args);
    }

    @Override
    public void showInfoSimple(String message, Object... args) {
        showMessage(FacesMessage.SEVERITY_INFO, message, null, args);
    }

    @Override
    public void render() {
        Faces.renderResponse();
    }

    @Override
    public void redirect(String viewLocation) {
        try{
            Faces.redirect(Faces.getExternalContext().getApplicationContextPath().concat(viewLocation));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Navigation redirectNavigation(String viewLocation) {
        return new BasicJsfRedirectNavigation(viewLocation);
    }

    @Override
    public void failValidation() {
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
    }
}
