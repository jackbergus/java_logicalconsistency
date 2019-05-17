package org.ufl.hypogator.jackb.streamutils.collectors;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Collects all the elements within the stream by a given key. It either returns the collected hashmap, or it restreams
 * it into a stream of Map.Entries
 *
 * @param <T> Objects to be mapped
 * @param <R> Key of the hashmap
 */
public class CollectToHashMultimap<T, R> extends CollectAndStream<T, HashMultimap<R, T>, Map.Entry<R, Collection<T>>> {

    private final HashMultimap<R, T> internal;
    private final Function<T, R> func;

    public CollectToHashMultimap(boolean doParallelize, Function<T, R> func) {
        super(doParallelize);
        this.func = func;
        internal = HashMultimap.create();
    }

    public CollectToHashMultimap<T, R> copy() {
        return new CollectToHashMultimap<>(isParallel(), func);
    }

    @Override
    protected IteratorWithOperations<Map.Entry<R, Collection<T>>> restream(HashMultimap<R, T> input) {
        Iterator<Map.Entry<R, Collection<T>>> it = input.asMap().entrySet().iterator();
        return new IteratorWithOperations<Map.Entry<R, Collection<T>>>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Map.Entry<R, Collection<T>> next() {
                return it.next();
            }
        };
    }

    @Override
    public Supplier<CollectAndStream<T, HashMultimap<R, T>, Map.Entry<R, Collection<T>>>> supplier() {
        return this::copy;
    }

    @Override
    public BiConsumer<CollectAndStream<T, HashMultimap<R, T>, Map.Entry<R, Collection<T>>>, T> accumulator() {
        return (x, t) -> ((CollectToHashMultimap) x).put(t);
    }

    private void put(T t) {
        put(func.apply(t), t);
    }

    private void put(R key, T value) {
        internal.put(key, value);
    }

    public CollectToHashMultimap<T, R> putAll(CollectToHashMultimap<T, R> x) {
        internal.putAll(x.internal);
        return this;
    }

    @Override
    public BinaryOperator<CollectAndStream<T, HashMultimap<R, T>, Map.Entry<R, Collection<T>>>> combiner() {
        return (x, y) -> this
                .copy()
                .putAll((CollectToHashMultimap<T, R>) x)
                .putAll((CollectToHashMultimap<T, R>) y);
    }

    @Override
    public Function<CollectAndStream<T, HashMultimap<R, T>, Map.Entry<R, Collection<T>>>, HashMultimap<R, T>> finisher() {
        return x -> ((CollectToHashMultimap<T, R>) x).internal;
    }
}
