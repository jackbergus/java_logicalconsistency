package org.ufl.hypogator.jackb.streamutils.iterator.filter;

import org.ufl.hypogator.jackb.streamutils.iterator.IteratorPreservingOperator;
import org.ufl.hypogator.jackb.streamutils.iterators.operations.FilteredIterator;

import java.util.Iterator;
import java.util.function.Predicate;

public abstract class Filter<T> implements Predicate<T>, IteratorPreservingOperator<T> {
    @Override
    public Iterator<T> apply(Iterator<T> tIterator) {
        return new FilteredIterator<>(tIterator, this);
    }
}
