package org.ufl.hypogator.jackb.streamutils.collectors;

import org.ufl.hypogator.jackb.streamutils.data.AlgebraSupport;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This collector returns a set containing elements of type T from a stream
 *
 * @param <T>
 */
public class CollectToSet<T> extends CollectAndStream<T, Set<T>, T> {

    Set<T> set;

    public CollectToSet(boolean parallelize) {
        this(parallelize, new HashSet<>());
    }

    public CollectToSet(boolean parallelize, Set<T> set) {
        super(parallelize);
        this.set = set;
    }

    private static <T> Set<T> compositeSet(Set<T> left, Set<T> right) {
        left.addAll(right);
        return left;
    }

    public CollectToSet(boolean b, Set<T> set, Set<T> set1) {
        this(b, compositeSet(set, set1));
    }

    @Override
    protected IteratorWithOperations<T> restream(Set<T> input) {
        return new AlgebraSupport<>(input.iterator());
    }

    @Override
    public Supplier<CollectAndStream<T, Set<T>, T>> supplier() {
        return () -> new CollectToSet<>(isParallel());
    }

    @Override
    public BiConsumer<CollectAndStream<T, Set<T>, T>, T> accumulator() {
        return (tSetTCollectAndStream, t) -> ((CollectToSet<T>) tSetTCollectAndStream).set.add(t);
    }


    @Override
    public BinaryOperator<CollectAndStream<T, Set<T>, T>> combiner() {
        return (left, right) -> new CollectToSet<T>(left.isParallel() || right.isParallel(), ((CollectToSet<T>) left).set, ((CollectToSet<T>) right).set);
    }


    @Override
    public Function<CollectAndStream<T, Set<T>, T>, Set<T>> finisher() {
        return x -> ((CollectToSet<T>) x).set;
    }
}
