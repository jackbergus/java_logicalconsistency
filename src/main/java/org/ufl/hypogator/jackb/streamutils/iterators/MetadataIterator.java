package org.ufl.hypogator.jackb.streamutils.iterators;

import org.ufl.hypogator.jackb.streamutils.functions.CurrifiedBiFunction;
import org.ufl.hypogator.jackb.streamutils.iterators.operations.MapIIterator;
import org.ufl.hypogator.jackb.streamutils.iterators.operations.MapIterator;
import javafx.util.Pair;

import java.util.Iterator;
import java.util.function.BiFunction;

public class MetadataIterator<Metadata, T> implements IteratorWithOperations<T> {
    private final Metadata meta;
    private final Iterator<T> iterator;

    protected MetadataIterator(Metadata meta, Iterator<T> iterator) {
        this.meta = meta;
        this.iterator = iterator;
    }

    public <K> IteratorWithOperations<K> map(BiFunction<Metadata, T, K> map) {
        CurrifiedBiFunction<Metadata, T, K> curry = new CurrifiedBiFunction<>(map);
        return new MapIterator<>(this, curry.apply(meta));
    }

    public <K> IteratorWithOperations<Pair<Long,K>> mapi(BiFunction<Metadata, T, K> map) {
        CurrifiedBiFunction<Metadata, T, K> curry = new CurrifiedBiFunction<>(map);
        return new MapIIterator<>(this, curry.apply(meta));
    }

    public Metadata getMeta() {
        return meta;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return iterator.next();
    }
}
