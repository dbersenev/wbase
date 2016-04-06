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

import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.molasdin.wbase.xml.parser.light.Element;
import org.molasdin.wbase.xml.parser.light.ErrorHandler;
import org.molasdin.wbase.xml.parser.light.ErrorType;
import org.molasdin.wbase.xml.parser.light.Parser;
import org.molasdin.wbase.xml.parser.light.exceptions.BadClosingTag;
import org.molasdin.wbase.xml.parser.light.exceptions.CharactersInClosing;
import org.molasdin.wbase.xml.parser.light.exceptions.DisallowedTagFound;
import org.molasdin.wbase.xml.parser.light.exceptions.ParserException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: dbersenev
 * Date: 07.02.13
 * Time: 13:43
 */
public class BasicParser implements Parser {

    private final static String EXCEPTION_INVALID_CONTENT = "Invalid text in tag name: <%s>";

    private Map<ErrorType, List<ErrorHandler>> handlers = new EnumMap<ErrorType, List<ErrorHandler>>(ErrorType.class);

    private final static Pattern attributePattern = Pattern.compile("([a-zA-Z_:][-a-zA-Z0-9_:.]*)\\s*=\\s*\"(.*?)\"");

    private final static Pattern chars = Pattern.compile(".+");

    private Predicate<String> predicate = new Predicate<String>() {
        @Override
        public boolean evaluate(String o) {
            return true;
        }
    };

    private Document doc;

    {
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void addErrorHandler(ErrorHandler handler, Set<ErrorType> types) {
        for (ErrorType type : types) {
            if (!handlers.containsKey(type)) {
                handlers.put(type, new ArrayList<ErrorHandler>());
            }
            handlers.get(type).add(handler);
        }
    }

    @Override
    public Element parse(String value) throws ParserException {
        Deque<BasicElement> elements = new LinkedList<BasicElement>();

        int index = 0;
        boolean leftAngleFound = false;
        int leftAngleIndex = 0;

        boolean possibleCloseTag = false;
        int closeSlashIndex = 0;

        BasicElement rootElement = new BasicElement();
        rootElement.setValid(true);
        elements.push(rootElement);

        while (index < value.length()) {
            if (value.charAt(index) == '<') {
                //if '<' is before '<'
                if (leftAngleFound) {
                    //treat it as '<' symbol
                    //build string from the first '<' till the current
                    String entry = value.substring(leftAngleIndex, index);
                    appendText(entry, elements.peekFirst());

                    invokeHandlers(value, leftAngleIndex, ErrorType.LESS_FOUND, "");
                }
                leftAngleFound = true;
                leftAngleIndex = index;
                possibleCloseTag = false;
            } else if (value.charAt(index) == '/') {
                //if '<' has been found
                if (leftAngleFound) {
                    //slash may be in closing tag
                    closeSlashIndex = index;
                    possibleCloseTag = true;
                } else {
                    appendText("/", elements.peekFirst());
                }
            } else if (value.charAt(index) == '>') {
                //if '>' without '<' before
                if (!leftAngleFound) {
                    //treat '>' as symbol
                    appendText(">", elements.peekFirst());
                    invokeHandlers(value, index, ErrorType.GREATER_FOUND, "");
                } else {
                    leftAngleFound = false;
                    BasicElement elem = elements.peekFirst();
                    //check if it is a closing tag
                    if (possibleCloseTag && isEmptyRange(value, leftAngleIndex + 1, closeSlashIndex)) {
                        String tag = StringUtils.trim(value.substring(leftAngleIndex + 2, index));
                        //if tag is most possible closing
                        if (!elem.isValid()) {
                            //check first opening
                            elem = elements.pop();
                            if (!elem.tagName().equals(tag)) {
                                BasicElement tmp = elem;
                                elem = elements.pop();
                                //check outer opening
                                if (!elem.tagName().equals(tag)) {
                                    throw new BadClosingTag(elem.tagName(), tag);
                                }
                                invokeHandlers(value, -1, ErrorType.NO_CLOSING, tmp.tagName());
                                elem.consumeContent(tmp);
                                elem.setValid(true);
                            }
                        } else {
                            //closing tag without opening
                            throw new BadClosingTag("", tag);
                        }
                        elem.setClosed(true);
                        elem.setValid(true);
                        elem.setValidClosing(true);

                    } else {
                        //tag is most possible opening or self closing
                        int rightOffset = index;
                        if (possibleCloseTag) {
                            //check if tag is closing but with characters between "<" and "/"
                            if (!elem.isValid()) {
                                String possibleTag = value.substring(closeSlashIndex + 1, index);
                                if (elem.tagName().equals(possibleTag)) {
                                    throw new CharactersInClosing(leftAngleIndex);
                                }
                            }

                            //check if "/" is in attributes
                            if (value.substring(closeSlashIndex + 1, rightOffset).trim().length() == 0) {
                                rightOffset = closeSlashIndex;
                            } else {
                                //tag is no closing
                                possibleCloseTag = false;
                            }

                        }


                        //possible start tag
                        String tagName = value.substring(leftAngleIndex + 1, rightOffset);

                        //if no tag but '<>'
                        if (tagName.length() == 0) {
                            //add them to characters
                            String entry = value.substring(leftAngleIndex, index + 1);
                            appendText(entry, elem);
                            invokeHandlers(value, leftAngleIndex, ErrorType.EMPTY_TAG_FOUND, entry);
                        } else {
                            Pair<String, List<Pair<String, String>>> tag = extractTag(tagName);
                            if (tag == null || tag.getLeft() == null) {
                                invokeHandlers(value, leftAngleIndex, ErrorType.INVALID_TEXT_IN_TAG_NAME, String.valueOf(index));
                                String entry = value.substring(leftAngleIndex, index + 1);
                                appendText(entry, elements.peekFirst());
                            } else {
                                tagName = tag.getLeft();
                                //if tag is allowed
                                if (!predicate.evaluate(tagName)) {
                                    throw new DisallowedTagFound(tagName);
                                }
                                //add new element with this tag
                                BasicElement newElem = new BasicElement();
                                newElem.setTagName(tagName);
                                newElem.setAttributes(tag.getRight());
                                elements.peekFirst().addChild(newElem);
                                if (possibleCloseTag) {
                                    newElem.setClosed(true);
                                    newElem.setShortenIfEmpty(true);
                                    newElem.setValid(true);
                                    newElem.setValidClosing(true);
                                } else {
                                    elements.push(newElem);
                                }
                            }
                        }
                    }

                    possibleCloseTag = false;
                }

            } else if (!leftAngleFound) {
                //characters block
                BasicElement elem = elements.peekFirst();
                //if other elements exist between tag characters parts
                elem.addCharacter(value.charAt(index));
               /* if (elementTextBreak) {
                    elementTextBreak = false;
                    elem.addCharacter(value.charAt(index));
                } else {
                    elem.lastCharacters().append(value.charAt(index));
                }*/
            }
            index++;
        }

        //if last '<' has not been closed
        if (leftAngleFound) {
            //treat it as symbol
            appendText(value.substring(leftAngleIndex, value.length()), elements.peekFirst());
            invokeHandlers(value, leftAngleIndex, ErrorType.LESS_FOUND, "");
        }

        //find unclosed elements
        if (elements.size() > 1) {
            for (BasicElement elem : elements) {
                if (elem == rootElement) {
                    continue;
                }
                if (!elem.isClosed() && !elem.isValid()) {
                    invokeHandlers(value, -1, ErrorType.NO_CLOSING, elem.tagName());
                    ((BasicElement) elem.parent()).consumeContent(elem);
                }
            }
        }
        return rootElement;
    }

    private boolean isEmptyRange(String value, int start, int end) {
        return value.substring(start, end).trim().length() == 0;
    }

    @Override
    public void setAllowedTagsPredicate(Predicate<String> predicate) {
        this.predicate = predicate;
    }

    private void invokeHandlers(String source, int index, ErrorType type, String value) throws ParserException {
        if (handlers.containsKey(type)) {
            for (ErrorHandler handler : handlers.get(type)) {
                if (!handler.error(Pair.of(source, index), Pair.of(type, value))) {
                    throw new ParserException();
                }
            }
        }
    }

    private void appendText(String text, BasicElement elem) {
        elem.addCharacters(text);
    }

    private Pair<String, List<Pair<String, String>>> extractTag(String value) {
        Scanner scanner = new Scanner(value);
        scanner.useDelimiter("\\s+");
        String tag = null;
        if (scanner.hasNext()) {
            tag = scanner.next();
            if (!isValidTag(tag)) {
                return null;
            }
        } else {
            return null;
        }

        if (tag.contains("/")) {
            return null;
        }

        List<Pair<String, String>> attributes = new ArrayList<Pair<String, String>>();

        while (scanner.hasNext()) {
            String part = scanner.next().trim();
            Matcher matcher = attributePattern.matcher(part);
            if (!matcher.matches()) {
                return null;
            }
            Pair<String, String> attribute = Pair.of(matcher.group(1), matcher.group(2));
            attributes.add(attribute);
        }

        return Pair.of(tag, attributes);
    }

    private boolean isValidTag(String tag) {
        try {
            doc.createElement(tag);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
