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

package org.molasdin.wbase.xml.parser.light.basic;

import org.apache.commons.lang3.tuple.Pair;
import org.molasdin.wbase.xml.parser.light.Element;
import org.molasdin.wbase.xml.parser.light.ElementsTreeWalker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * User: dbersenev
 * Date: 07.02.13
 * Time: 13:29
 */
public class BasicElement implements Element {
    private String tagName = "";

    private Element parent;

    private List<StringBuilder> characters = new ArrayList<StringBuilder>();

    private boolean isClosed;

    private boolean shorten;

    private List<Pair<String, String>> attributes = new ArrayList<Pair<String, String>>();

    private List<Element> children = new ArrayList<Element>();

    private List<Object> positions = new ArrayList<Object>();

    private final static String START_FORMAT = "<%s>";
    private final static String CLOSED_FORMAT = "<%s/>";
    private final static String END_FORMAT = "</%s>";

    private boolean valid;

    private boolean isValidClosing;

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setClosed(boolean valid) {
        this.isClosed = valid;
    }

    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void setShortenIfEmpty(boolean flag) {
        shorten = flag;
    }

    @Override
    public boolean isValidClosing() {
        return isValidClosing;
    }

    public void setValidClosing(boolean validClosing) {
        isValidClosing = validClosing;
    }

    @Override
    public void setAttributes(List<Pair<String, String>> attributes) {
        this.attributes = attributes;
    }

    @Override
    public List<Pair<String, String>> attributes() {
        return attributes;
    }

    @Override
    public boolean hasAttribute(String name) {
        return attribute(name) != null;
    }

    @Override
    public String attribute(String name) {
        for (Pair<String, String> entry : attributes()) {
            if (entry.getRight().equals(name)) {
                return entry.getRight();
            }
        }
        return null;
    }

    public void addChild(Element element) {
        children.add(element);
        positions.add(element);
        element.setParent(this);
    }

    public void setParent(Element element) {
        this.parent = element;
    }

    public Element parent() {
        return parent;
    }

    public List<Element> children() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public List<Element> childrenByTag(String name) {
        List<Element> result = new LinkedList<Element>();
        for (Element elm : children) {
            if (elm.tagName().equalsIgnoreCase(name)) {
                result.add(elm);
            }
        }
        return result;
    }

    public void addCharacters(String value) {
//        addString(value, false);
        if (positions.size() > 0 &&
                positions.get(positions.size() - 1) instanceof String) {
            characters.get(characters.size() - 1).append(value);
        } else {
            addString(value, false);
        }
    }

    public void addCharacter(char value) {
        addCharacters(String.valueOf(value));
    }

    public StringBuilder lastCharacters() {
        return characters.size() > 0 ? characters.get(characters.size() - 1) : null;
    }

    public String tagName() {
        return this.tagName;
    }

    public void setTagName(String name) {
        this.tagName = name;
    }

    public void setValue(String value) {
        positions.removeAll(characters);
        characters.clear();
        addString(value, true);
    }

    private void addString(String value, boolean first) {
        StringBuilder builder = new StringBuilder();
        builder.append(value);
        characters.add(builder);
        if (first) {
            positions.add(0, builder);
        } else {
            positions.add(builder);
        }
    }

    public String value() {
        StringBuilder builder = new StringBuilder();
        for (StringBuilder entry : characters) {
            builder.append(entry.toString());
        }
        return builder.toString();
    }

    @Override
    public void remove() {
        this.parent().removeChild(this);
    }

    @Override
    public void removeChild(Element element) {
        children.remove(element);
        positions.remove(element);
    }

    public void walkTree(ElementsTreeWalker walker) {
        if (!walker.process(this)) {
            return;
        }
        for (int i = 0; i < children.size(); i++) {
            children.get(i).walkTree(walker);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Object entry : positions) {
            builder.append(entry.toString());
        }

        if (tagName().length() > 0) {
            StringBuilder withNameBuilder = new StringBuilder();
            withNameBuilder.append(String.format("<%s", this.tagName()));
            if (!attributes().isEmpty()) {
                withNameBuilder.append(' ');
            }
            boolean addSpace = false;
            for (Pair<String, String> attrib : attributes()) {
                if (addSpace) {
                    withNameBuilder.append(' ');
                }
                withNameBuilder.append(String.format("%s = \"%s\"", attrib.getLeft(), attrib.getRight()));
                addSpace = true;
            }

            if (positions.isEmpty() && isClosed() && shorten) {
                withNameBuilder.append("/>");
            } else {
                withNameBuilder.append('>');
                if (isClosed()) {
                    withNameBuilder.append(builder);
                    withNameBuilder.append(String.format("</%s>", this.tagName()));
                }
            }
            return withNameBuilder.toString();
        }

        return builder.toString();
    }

    public void consumeContent(BasicElement element) {
        if (element.parent() != this) {
            return;
        }

        for (int i = 0; i < element.positions.size(); i++) {
            Object item = element.positions.get(i);
            if (item instanceof Element) {
                addChild((Element) item);
            } else {
                addCharacters(item.toString());
            }
        }
        element.positions.clear();
        element.children.clear();
        element.characters.clear();
    }
}
