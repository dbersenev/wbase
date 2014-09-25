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

package org.molasdin.wbase.xml.parser.light.exceptions;

/**
 * User: dbersenev
 * Date: 07.02.13
 * Time: 13:44
 */
public class DisallowedTagFound extends ParserException {
    private String tag;

    public DisallowedTagFound(String tag) {
        this.tag = tag;
    }

    public String tag(){
        return tag;
    }
}
