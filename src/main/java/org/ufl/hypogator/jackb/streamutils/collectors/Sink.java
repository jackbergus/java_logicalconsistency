package org.ufl.hypogator.jackb.streamutils.collectors;

import com.google.common.collect.Lists;
import org.ufl.hypogator.jackb.streamutils.data.AlgebraSupport;
import org.ufl.hypogator.jackb.streamutils.functions.EmptyConsumer;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public class Sink<T> extends CollectAndStream<T, Object, Object> {

    public Sink(boolean parallelize) {
        super(parallelize);
    }

    @Override
    protected IteratorWithOperations<Object> restream(Object input) {
        return new AlgebraSupport<>(Lists.newArrayList(input).iterator());
    }

    @Override
    public Supplier<CollectAndStream<T, Object, Object>> supplier() {
        return () -> new Sink<>(isParallel());
    }

    @Override
    public BiConsumer<CollectAndStream<T, Object, Object>, T> accumulator() {
        return EmptyConsumer.instance();
    }

    private Object increment() {
        return true;
    }

    @Override
    public BinaryOperator<CollectAndStream<T, Object, Object>> combiner() {
        return (left, right) -> new Sink<>(left.isParallel() || right.isParallel());
    }

    public Object get() {
        return true;
    }

    @Override
    public Function<CollectAndStream<T, Object, Object>, Object> finisher() {
        return x -> true;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return new HashSet<>();
    }
}
