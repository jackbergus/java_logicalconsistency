package org.ufl.hypogator.jackb.streamutils.iterators.operations;

import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.ArrayList;
import java.util.Iterator;

public class UnionIterator<T> implements IteratorWithOperations<T> {

    private IteratorWithOperations<T> current;
    private Iterator<IteratorWithOperations<T>> cursor;

    private UnionIterator(Iterable<IteratorWithOperations<T>> iterators) {
        if (iterators == null) throw new IllegalArgumentException("org.ufl.hypogator.jackb.streamutils.iterators is null");
        this.cursor = iterators.iterator();
    }

    private IteratorWithOperations<T> findNext() {
        while (cursor.hasNext()) {
            current = cursor.next();
            if (current != null && current.hasNext()) return current;
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

    public static <T> Builder<T> start() {
        return new Builder<>();
    }

    public static <T> Builder<T> with(IteratorWithOperations<T> ith) {
        return new Builder<>(ith);
    }

    public static class Builder<T> {
        ArrayList<IteratorWithOperations<T>> elements;

        protected Builder() {
            elements = new ArrayList<>();
        }

        protected Builder(IteratorWithOperations<T> first) {
            this();
            elements.add(first);
        }

        public Builder<T> with(IteratorWithOperations<T> ith) {
            elements.add(ith);
            return this;
        }

        public IteratorWithOperations<T> done() {
            return new UnionIterator<>(elements);
        }

        public void withAll(Builder<T> acc) {
            elements.addAll(acc.elements);
        }
    }

}