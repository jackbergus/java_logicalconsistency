package org.ufl.hypogator.jackb.streamutils.data;

import java.util.Iterator;

public class ArraySupport<T> implements Iterator<T> {

    private final T[] array;
    private int i = 0;
    private final int n;

    public ArraySupport(T... array) {
        this.array = array;
        this.n = array != null ? array.length : 0;
    }

    @Override
    public boolean hasNext() {
        return n != i;
    }

    @Override
    public T next() {
        return array[i++];
    }
}
