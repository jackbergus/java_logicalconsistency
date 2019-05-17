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

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public class ObjectMultiGroupByWithValue<T, Key, Value> extends CollectAndStream<T, HashMultimap<Key, Value>, Map.Entry<Key, Collection<Value>>> {

    private final HashMultimap<Key, Value> internal;
    private final Function<T, Key> func;
    private final Function<T, Value> func2;

    public ObjectMultiGroupByWithValue(boolean doParallelize, Function<T, Key> func, Function<T, Value> func2) {
        super(doParallelize);
        this.func = func;
        this.func2 = func2;
        internal = HashMultimap.create();
    }

    public ObjectMultiGroupByWithValue<T, Key, Value> copy() {
        return new ObjectMultiGroupByWithValue<>(isParallel(), func, func2);
    }

    @Override
    protected IteratorWithOperations<Map.Entry<Key, Collection<Value>>> restream(HashMultimap<Key, Value> input) {
        Iterator<Map.Entry<Key, Collection<Value>>> it = input.asMap().entrySet().iterator();
        return new IteratorWithOperations<Map.Entry<Key, Collection<Value>>>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Map.Entry<Key, Collection<Value>> next() {
                return it.next();
            }
        };
    }

    @Override
    public Supplier<CollectAndStream<T, HashMultimap<Key, Value>, Map.Entry<Key, Collection<Value>>>> supplier() {
        return this::copy;
    }

    @Override
    public BiConsumer<CollectAndStream<T, HashMultimap<Key, Value>, Map.Entry<Key, Collection<Value>>>, T> accumulator() {
        return (x, t) -> ((ObjectMultiGroupByWithValue) x).put(t);
    }

    private void put(T t) {
        internal.put(func.apply(t), func2.apply(t));
    }

    public ObjectMultiGroupByWithValue<T, Key, Value> putAll(ObjectMultiGroupByWithValue<T, Key, Value> x) {
        internal.putAll(x.internal);
        return this;
    }

    @Override
    public BinaryOperator<CollectAndStream<T, HashMultimap<Key, Value>, Map.Entry<Key, Collection<Value>>>> combiner() {
        return (x, y) -> this
                .copy()
                .putAll((ObjectMultiGroupByWithValue<T, Key, Value>) x)
                .putAll((ObjectMultiGroupByWithValue<T, Key, Value>) y);
    }

    @Override
    public Function<CollectAndStream<T, HashMultimap<Key, Value>, Map.Entry<Key, Collection<Value>>>, HashMultimap<Key, Value>> finisher() {
        return x -> ((ObjectMultiGroupByWithValue<T, Key, Value>) x).internal;
    }
}
