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

import java.io.Serializable;

/**
 * Created by dbersenev on 18.06.2014.
 */
public interface CommonPage extends Serializable {
    String messageBoxId();

    void setShowMessagesInDialog(Boolean flag);
    Boolean isShowMessagesInDialog();

    void showError(String shortMessage, String details, Object... args);
    void showWarning(String shortMessage, String details, Object... args);
    void showInfo(String shortMessage, String details, Object... args);

    void showErrorSimple(String message, Object... args);
    void showWarningSimple(String message, Object... args);
    void showInfoSimple(String message, Object... args);

    void failValidation();

    void render();
    void redirect(String viewLocation);
    Navigation redirectNavigation(String viewLocation);
}
