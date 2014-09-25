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

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * User: dbersenev
 * Date: 06.02.13
 * Time: 17:25
 */
public interface Element {

    List<Element> children();

    List<Element> childrenByTag(String name);

    //Emphasis for sequence
    void setAttributes(List<Pair<String, String>> attributes);

    List<Pair<String, String>> attributes();

    boolean hasAttribute(String name);

    String attribute(String name);

    void addChild(Element element);

    boolean isClosed();

    void setClosed(boolean closed);

    void setShortenIfEmpty(boolean flag);

    boolean isValidClosing();

    void addCharacters(String value);

    StringBuilder lastCharacters();

    void setTagName(String name);

    String tagName();

    Element parent();

    void setParent(Element element);

    void setValue(String value);

    String value();

    void walkTree(ElementsTreeWalker walker);

    void remove();

    void removeChild(Element element);
}
