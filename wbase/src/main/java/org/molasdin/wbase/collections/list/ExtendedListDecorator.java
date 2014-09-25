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

package org.molasdin.wbase.collections.list;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.list.AbstractListDecorator;
import org.molasdin.wbase.ReflectionHelper;
import org.molasdin.wbase.storage.Cursor;
import org.molasdin.wbase.storage.Order;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dbersenev on 16.04.2014.
 */
public class ExtendedListDecorator<T> extends AbstractListDecorator<T> implements ExtendedList<T> {

    private Comparator<T> comparator;

    public ExtendedListDecorator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public ExtendedListDecorator(List<T> list, Comparator<T> comparator) {
        super(list);
        this.comparator = comparator;
    }

    @Override
    public void sort(Order order) {
        if (Order.ASC.equals(order)){
            Collections.sort(decorated(), comparator);
        } else {
            Collections.sort(decorated(), Collections.reverseOrder(comparator));
        }
    }

    @SuppressWarnings("unchecked")
    public void sort(final String property, Order order) {
        if(decorated().isEmpty()){
            return;
        }

        T item = decorated().get(0);
        Comparator<T> tmp = null;
        if(ReflectionHelper.hasFunction(property, item)){
            final Transformer<T, Object> transformer = new Transformer<T, Object>() {
                @Override
                public Object transform(T o) {
                    return ReflectionHelper.functionValue(property, o);
                }
            };
            Object val = transformer.transform(item);
            final boolean isNatural = ReflectionHelper.supportsNaturalOrdering(val);
            tmp = new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    Object firstResult = transformer.transform(o1);
                    Object secondResult = transformer.transform(o2);
                    if(isNatural){
                        Comparable f = (Comparable)firstResult;
                        Comparable s = (Comparable)secondResult;
                        return f.compareTo(s);
                    }

                    String f = ConvertUtils.convert(firstResult);
                    String s = ConvertUtils.convert(secondResult);
                    return f.compareTo(s);
                }
            };
        } else if(PropertyUtils.isReadable(item, property)){
           tmp = new BeanComparator(property);
        } else {
            throw new RuntimeException("No property");
        }

        Collections.sort(decorated(), tmp);
    }

    @Override
    public Cursor<T> newCursor() {
        return null;
    }

    @Override
    public Cursor<T> currentCursor() {
        return null;
    }
}
