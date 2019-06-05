/*
 * ObjectMultiGroupByWithValue.java
 * This file is part of aida_scraper
 *
 * Copyright (C) 2018 giacomo
 *
 * aida_scraper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * aida_scraper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aida_scraper. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.ufl.hypogator.jackb.streamutils.collectors;

import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public class ObjectGroupByWithValue<T, Key, Value> extends CollectAndStream<T, HashMap<Key, Value>, Map.Entry<Key, Value>> {

    private final HashMap<Key, Value> internal;
    private final Function<T, Key> func;
    private final Function<T, Value> func2;

    public ObjectGroupByWithValue(boolean doParallelize, Function<T, Key> func, Function<T, Value> func2) {
        super(doParallelize);
        this.func = func;
        this.func2 = func2;
        internal = new HashMap<>();
    }

    public ObjectGroupByWithValue<T, Key, Value> copy() {
        return new ObjectGroupByWithValue<>(isParallel(), func, func2);
    }

    @Override
    protected IteratorWithOperations<Map.Entry<Key, Value>> restream(HashMap<Key, Value> input) {
        Iterator<Map.Entry<Key, Value>> it = input.entrySet().iterator();
        return new IteratorWithOperations<Map.Entry<Key, Value>>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Map.Entry<Key, Value> next() {
                return it.next();
            }
        };
    }

    @Override
    public Supplier<CollectAndStream<T, HashMap<Key, Value>, Map.Entry<Key, Value>>> supplier() {
        return this::copy;
    }

    @Override
    public BiConsumer<CollectAndStream<T, HashMap<Key, Value>, Map.Entry<Key, Value>>, T> accumulator() {
        return (x, t) -> ((ObjectGroupByWithValue) x).put(t);
    }

    private void put(T t) {
        internal.put(func.apply(t), func2.apply(t));
    }

    public ObjectGroupByWithValue<T, Key, Value> putAll(ObjectGroupByWithValue<T, Key, Value> x) {
        internal.putAll(x.internal);
        return this;
    }

    @Override
    public BinaryOperator<CollectAndStream<T, HashMap<Key, Value>, Map.Entry<Key, Value>>> combiner() {
        return (x, y) -> this
                .copy()
                .putAll((ObjectGroupByWithValue<T, Key, Value>) x)
                .putAll((ObjectGroupByWithValue<T, Key, Value>) y);
    }

    @Override
    public Function<CollectAndStream<T, HashMap<Key, Value>, Map.Entry<Key, Value>>, HashMap<Key, Value>> finisher() {
        return x -> ((ObjectGroupByWithValue<T, Key, Value>) x).internal;
    }
}
