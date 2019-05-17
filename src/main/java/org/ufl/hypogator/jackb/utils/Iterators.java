/*
 * Iterators.java
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

package org.ufl.hypogator.jackb.utils;

import java.util.Iterator;

public class Iterators<T> implements Iterator<T> {

    private Iterator<T> current;
    private Iterator<Iterator<T>> cursor;

    public Iterators(Iterator<Iterator<T>> iterators) {
        if (iterators == null) throw new IllegalArgumentException("iterators is null");
        this.cursor = iterators;
    }

    public Iterators(Iterable<Iterator<T>> iterators) {
        if (iterators == null) throw new IllegalArgumentException("iterators is null");
        this.cursor = iterators.iterator();
    }

    private Iterator<T> findNext() {
        while (cursor.hasNext()) {
            current = cursor.next();
            if (current.hasNext()) return current;
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        if (current == null || !current.hasNext()) {
            current = findNext();
        }
        return (current != null && current.hasNext());
    }

    @Override
    public T next() {
        return current.next();
    }

    @Override
    public void remove() {
        if (current != null) {
            current.remove();
        }
    }
}