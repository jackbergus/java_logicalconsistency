package org.ufl.hypogator.jackb.streamutils.collectors;

import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;
import org.ufl.hypogator.jackb.streamutils.iterators.operations.UnionIterator;

import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public class FlatMap<T, R> extends CollectAndStream<T, FlatMap<T, R>, R>
        implements Function<T, IteratorWithOperations<R>> {

    private final UnionIterator.Builder<R> acc;
    private final Function<T, IteratorWithOperations<R>> func;

    public FlatMap(boolean doParallelize, Function<T, IteratorWithOperations<R>> func) {
        super(doParallelize);
        this.func = func;
        this.acc = UnionIterator.start();
    }

    public FlatMap<T, R> copy() {
        return new FlatMap<>(isParallel(), func);
    }

    @Override
    protected IteratorWithOperations<R> restream(FlatMap<T, R> input) {
        return input.acc.done();
    }

    @Override
    public Supplier<CollectAndStream<T, FlatMap<T, R>, R>> supplier() {
        return this::copy;
    }

    @Override
    public BiConsumer<CollectAndStream<T, FlatMap<T, R>, R>, T> accumulator() {
        return (fm, t) -> ((FlatMap<T, R>) fm).process(t);
    }

    private void process(T t) {
        acc.with(func.apply(t));
    }

    public FlatMap<T, R> addAll(FlatMap<T, R> map) {
        acc.withAll(map.acc);
        return this;
    }

    @Override
    public BinaryOperator<CollectAndStream<T, FlatMap<T, R>, R>> combiner() {
        return (l, r) -> copy().addAll(((FlatMap<T, R>) l)).addAll(((FlatMap<T, R>) r));
    }

    @Override
    public Function<CollectAndStream<T, FlatMap<T, R>, R>, FlatMap<T, R>> finisher() {
        return x -> (((FlatMap<T, R>) x));
    }

    @Override
    public IteratorWithOperations<R> apply(T t) {
        return func.apply(t);
    }
}
