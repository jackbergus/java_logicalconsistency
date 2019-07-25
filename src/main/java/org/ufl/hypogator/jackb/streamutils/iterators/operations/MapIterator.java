package org.ufl.hypogator.jackb.streamutils.iterators.operations;

import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.Iterator;
import java.util.function.Function;

public final class MapIterator<S, T> implements IteratorWithOperations<T> {
    private final Iterator<S> request;
    private final Function<S, T> map;

    public MapIterator(Iterator<S> request, Function<S, T> map) {
        this.request = request;
        this.map = map;
    }

    @Override
    public boolean hasNext() {
        return request.hasNext();
    }

    @Override
    public T next() {
        return map.apply(request.next());
    }
}
