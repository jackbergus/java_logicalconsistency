package org.ufl.hypogator.jackb.streamutils.collectors;

import org.ufl.hypogator.jackb.streamutils.functions.ObjectCombiner;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.HashMap;
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
public class CollectToHashMap<T extends ObjectCombiner<T>, R> extends CollectAndStream<T, HashMap<R, T>, Map.Entry<R, T>> {

    private final HashMap<R, T> internal;
    private final Function<T, R> func;

    public CollectToHashMap(boolean doParallelize, Function<T, R> func) {
        super(doParallelize);
        this.func = func;
        internal = new HashMap<>();
    }

    public CollectToHashMap<T, R> copy() {
        return new CollectToHashMap<>(isParallel(), func);
    }

    @Override
    protected IteratorWithOperations<Map.Entry<R, T>> restream(HashMap<R, T> input) {
        Iterator<Map.Entry<R, T>> it = input.entrySet().iterator();
        return new IteratorWithOperations<Map.Entry<R, T>>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Map.Entry<R, T> next() {
                return it.next();
            }
        };
    }

    @Override
    public Supplier<CollectAndStream<T, HashMap<R, T>, Map.Entry<R, T>>> supplier() {
        return this::copy;
    }

    @Override
    public BiConsumer<CollectAndStream<T, HashMap<R, T>, Map.Entry<R, T>>, T> accumulator() {
        return (x, t) -> ((CollectToHashMap) x).put(t);
    }

    private void put(T t) {
        put(func.apply(t), t);
    }

    private void put(R key, T value) {
        internal.compute(key, (k, v) -> {
            if (v == null) return value;
            else return v.apply(value);
        });
    }

    public CollectToHashMap<T, R> putAll(CollectToHashMap<T, R> x) {
        for (Map.Entry<R, T> y : x.internal.entrySet()) {
            put(y.getKey(), y.getValue());
        }
        return this;
    }

    @Override
    public BinaryOperator<CollectAndStream<T, HashMap<R, T>, Map.Entry<R, T>>> combiner() {
        return (x, y) -> this
                .copy()
                .putAll((CollectToHashMap<T, R>) x)
                .putAll((CollectToHashMap<T, R>) y);
    }

    @Override
    public Function<CollectAndStream<T, HashMap<R, T>, Map.Entry<R, T>>, HashMap<R, T>> finisher() {
        return x -> ((CollectToHashMap<T, R>) x).internal;
    }
}
