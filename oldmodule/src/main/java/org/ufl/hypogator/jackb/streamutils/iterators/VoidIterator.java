package org.ufl.hypogator.jackb.streamutils.iterators;

public class VoidIterator<T> implements IteratorWithOperations<T> {
    private VoidIterator() {
    }

    private static VoidIterator iterator = new VoidIterator();

    public static <T> VoidIterator<T> getInstance() {
        return iterator;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        return null;
    }

    public static <K> VoidIterator<K> get() {
        return (VoidIterator<K>) iterator;
    }
}
