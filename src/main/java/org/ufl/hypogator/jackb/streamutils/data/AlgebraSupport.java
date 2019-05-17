package org.ufl.hypogator.jackb.streamutils.data;

import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.Iterator;

/**
 * Use DataStreamer instead
 * @param <T>
 */
public class AlgebraSupport<T> implements IteratorWithOperations<T> {
    private final Iterator<T> ls;

    public AlgebraSupport(Iterator<T> ls) {
        this.ls = ls;
    }

    @Override
    public boolean hasNext() {
        return ls.hasNext();
    }

    @Override
    public T next() {
        return ls.next();
    }
}
