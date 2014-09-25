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

package org.molasdin.wbase.xml.parser.light;

/**
 * User: dbersenev
 * Date: 25.02.13
 * Time: 12:40
 */
public enum ErrorType {
    LESS_FOUND,
    GREATER_FOUND,
    EMPTY_TAG_FOUND,
    NO_CLOSING,
    INVALID_TEXT_IN_TAG_NAME

}
