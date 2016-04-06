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

package org.molasdin.wbase.jsf.messages;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.omnifaces.util.Messages;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class MessageTools {
    public final static String MESSAGE_BOX_ID = "commonMessageBoxId";

    public static List<Pair<String, FacesMessage>> facesMessages(){
        List<Pair<String, FacesMessage>> messages = new LinkedList<Pair<String, FacesMessage>>();
        FacesContext ctx = FacesContext.getCurrentInstance();
        Iterator<String> messageIds = ctx.getClientIdsWithMessages();
        while(messageIds.hasNext()){
            String id = messageIds.next();
            List<FacesMessage> msgs = ctx.getMessageList(id);
            for(FacesMessage msg: msgs){
                Pair<String, FacesMessage> messagesForId = new ImmutablePair<String, FacesMessage>(id, msg);
                messages.add(messagesForId);
            }
        }

        return messages;
    }


    public static void addError(String message){
        addError(message, null);
    }

    public static void addInfo(String message){
        addInfo(message, null);
    }

    public static void addError(String message, String detail){
        addMessage(message, FacesMessage.SEVERITY_FATAL, detail);
    }

    public static void addInfo(String message, String detail){
        addMessage(message, FacesMessage.SEVERITY_INFO, detail);
    }

    private static void addMessage(String message, FacesMessage.Severity severity, String details){
        FacesMessage msg = new FacesMessage(severity, message, details);
        Messages.add(MESSAGE_BOX_ID, msg);
    }
}
