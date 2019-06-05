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
 * @param <V> Value
 * @param <K> Key
 */
public class HashMultimapCollect<K, V> extends CollectAndStream<HashMultimap<K, V>, HashMultimap<K, V>, Map.Entry<K, Collection<V>>> {

    private final HashMultimap<K, V> internal;

    public HashMultimapCollect(boolean doParallelize) {
        super(doParallelize);
        internal = HashMultimap.create();
    }

    public HashMultimapCollect<K, V> copy() {
        return new HashMultimapCollect<>(isParallel());
    }

    @Override
    protected IteratorWithOperations<Map.Entry<K, Collection<V>>> restream(HashMultimap<K, V> input) {
        Iterator<Map.Entry<K, Collection<V>>> it = input.asMap().entrySet().iterator();
        return new IteratorWithOperations<Map.Entry<K, Collection<V>>>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Map.Entry<K, Collection<V>> next() {
                return it.next();
            }
        };
    }

    @Override
    public Supplier<CollectAndStream<HashMultimap<K, V>, HashMultimap<K, V>, Map.Entry<K, Collection<V>>>> supplier() {
        return this::copy;
    }

    @Override
    public BiConsumer<CollectAndStream<HashMultimap<K, V>, HashMultimap<K, V>, Map.Entry<K, Collection<V>>>, HashMultimap<K, V>> accumulator() {
        return (x, t) -> ((HashMultimapCollect<K, V>) x).putAll(t);
    }

    private void putAll(HashMultimap<K, V> t) {
        internal.putAll(t);
    }

    private void put(K key, V value) {
        internal.put(key, value);
    }

    public HashMultimapCollect<K, V> putAll(HashMultimapCollect<K, V> x) {
        internal.putAll(x.internal);
        return this;
    }

    @Override
    public BinaryOperator<CollectAndStream<HashMultimap<K, V>, HashMultimap<K, V>, Map.Entry<K, Collection<V>>>> combiner() {
        return (x, y) -> this
                .copy()
                .putAll((HashMultimapCollect<K, V>) x)
                .putAll((HashMultimapCollect<K, V>) y);
    }

    @Override
    public Function<CollectAndStream<HashMultimap<K, V>, HashMultimap<K, V>, Map.Entry<K, Collection<V>>>, HashMultimap<K, V>> finisher() {
        return x -> ((HashMultimapCollect<K, V>) x).internal;
    }
}
