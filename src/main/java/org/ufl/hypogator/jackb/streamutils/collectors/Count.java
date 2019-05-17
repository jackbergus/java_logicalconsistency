package org.ufl.hypogator.jackb.streamutils.collectors;

import com.google.common.collect.Lists;
import org.ufl.hypogator.jackb.streamutils.data.AlgebraSupport;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This collector simply counts the elements within the stream
 *
 * @param <T>
 */
public class Count<T> extends CollectAndStream<T, Long, Long> {

    AtomicLong counter;

    public Count(boolean parallelize, long i) {
        super(parallelize);
        counter = new AtomicLong(i);
    }

    @Override
    protected IteratorWithOperations<Long> restream(Long input) {
        return new AlgebraSupport<>(Lists.newArrayList(input).iterator());
    }

    @Override
    public Supplier<CollectAndStream<T, Long, Long>> supplier() {
        return () -> new Count<>(isParallel(), 0);
    }

    @Override
    public BiConsumer<CollectAndStream<T, Long, Long>, T> accumulator() {
        return (tLongLongCollectAndStream, t) -> ((Count<T>) tLongLongCollectAndStream).increment();
    }

    private void increment() {
        counter.incrementAndGet();
    }

    @Override
    public BinaryOperator<CollectAndStream<T, Long, Long>> combiner() {
        return (left, right) -> new Count<T>(left.isParallel() || right.isParallel(),
                ((Count<T>) left).get() + ((Count<T>) right).get());
    }

    public long get() {
        return counter.get();
    }

    @Override
    public Function<CollectAndStream<T, Long, Long>, Long> finisher() {
        return x -> ((Count<T>) x).get();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return new HashSet<>();
    }
}
