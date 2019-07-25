/*
 * MapIIterator.java
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

package org.ufl.hypogator.jackb.streamutils.iterators.operations;

import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;
import javafx.util.Pair;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public final class MapIIterator<S, T> implements IteratorWithOperations<Pair<Long, T>> {
    private final Iterator<S> request;
    private final Function<S, T> map;
    private AtomicLong rowCounter;

    public MapIIterator(Iterator<S> request, Function<S, T> map) {
        this.request = request;
        this.map = map;
        rowCounter = new AtomicLong(0);
    }

    @Override
    public boolean hasNext() {
        return request.hasNext();
    }

    @Override
    public Pair<Long, T> next() {
        return new Pair<>(rowCounter.getAndIncrement(), map.apply(request.next()));
    }
}
