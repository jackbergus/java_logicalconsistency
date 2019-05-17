package org.ufl.aida.ldc.dbloader;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Implements an iterator acting like a map without the need of streaming, that is applying the map at runtime over
 * each object that we want to traverse through the iterator
 *
 * @param <S>   Source data type
 * @param <T>   Target data type
 */
public abstract class MapIterator<S, T> implements Function<S, T>, Iterator<T> {

    private final Iterator<S> source;

    /**
     * @param source    To-be mapped iterator
     */
    public MapIterator(Iterator<S> source) {
        this.source = source;
    }

    @Override
    public boolean hasNext() {
        return source.hasNext();
    }

    /**
     * @return Trasformed object towards the required type
     */
    @Override
    public T next() {
        return apply(source.next());
    }

}
