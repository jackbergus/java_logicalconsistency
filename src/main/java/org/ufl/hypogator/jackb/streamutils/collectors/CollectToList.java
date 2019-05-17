/*
 * CollectToList.java
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

import org.ufl.hypogator.jackb.streamutils.data.AlgebraSupport;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This collector returns a set containing elements of type T from a stream
 *
 * @param <T>
 */
public class CollectToList<T> extends CollectAndStream<T, List<T>, T> {

    List<T> set;

    public CollectToList(boolean parallelize) {
        this(parallelize, new ArrayList<>());
    }

    public CollectToList(boolean parallelize, List<T> set) {
        super(parallelize);
        this.set = set;
    }

    private static <T> List<T> compositeSet(List<T> left, List<T> right) {
        left.addAll(right);
        return left;
    }

    public CollectToList(boolean b, List<T> set, List<T> set1) {
        this(b, compositeSet(set, set1));
    }

    @Override
    protected IteratorWithOperations<T> restream(List<T> input) {
        return new AlgebraSupport<>(input.iterator());
    }

    @Override
    public Supplier<CollectAndStream<T, List<T>, T>> supplier() {
        return () -> new CollectToList<>(isParallel());
    }

    @Override
    public BiConsumer<CollectAndStream<T, List<T>, T>, T> accumulator() {
        return (tSetTCollectAndStream, t) -> ((CollectToList<T>) tSetTCollectAndStream).set.add(t);
    }


    @Override
    public BinaryOperator<CollectAndStream<T, List<T>, T>> combiner() {
        return (left, right) -> new CollectToList<T>(left.isParallel() || right.isParallel(), ((CollectToList<T>) left).set, ((CollectToList<T>) right).set);
    }


    @Override
    public Function<CollectAndStream<T, List<T>, T>, List<T>> finisher() {
        return x -> ((CollectToList<T>) x).set;
    }
}
