package org.ufl.hypogator.jackb.utils;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

public class FilteredIterator<E> implements Iterator<E> {
    private final Iterator<E> iterator;
    private final Predicate<E> filter;

    private boolean hasNext = true;
    private E next;

    /**
     *
     * @param iterator      Java iterator over the elements
     * @param filter        Filtering all the elements within the stream. If this is set to null, then all the elments are preserved
     */
    public FilteredIterator(final Iterator<E> iterator, final Predicate<E> filter) {
        this.iterator = iterator;
        Objects.requireNonNull(iterator);
        this.filter = filter;
        this.findNext();
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public E next() {
        E returnValue = this.next;
        this.findNext();
        return returnValue;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void findNext() {
        while (this.iterator.hasNext()) {
            this.next = iterator.next();
            if ((this.filter == null) || this.filter.test(this.next)) {
                return;
            }
        }
        this.next = null;
        this.hasNext = false;
    }

    /*private static final class AcceptAllFilter<T> implements Predicate<T> {
        public boolean test(final T type) {
            return true;
        }
    }*/
}