package org.ufl.hypogator.jackb.streamutils.collectors;

import com.google.common.collect.Lists;
import org.ufl.hypogator.jackb.streamutils.data.AlgebraSupport;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This collector simply counts the elements within the stream
 *
 * @param <T>
 */
public class SpaceJoin<T> extends CollectAndStream<T, StringBuilder, String> {

    StringBuilder counter;

    public SpaceJoin() {
        super(false);
        counter = new StringBuilder();
    }

    public SpaceJoin(String left, String right) {
        super(false);
        counter = new StringBuilder();
        counter.append(left);
        counter.append(" ");
        counter.append(right);
    }

    @Override
    protected IteratorWithOperations<String> restream(StringBuilder input) {
        return new AlgebraSupport<>(Lists.newArrayList(input.toString()).iterator());
    }

    @Override
    public Supplier<CollectAndStream<T, StringBuilder, String>> supplier() {
        return SpaceJoin::new;
    }

    @Override
    public BiConsumer<CollectAndStream<T, StringBuilder, String>, T> accumulator() {
        return (tStringStringCollectAndStream, t) -> ((SpaceJoin<T>) tStringStringCollectAndStream).increment(t);
    }

    private void increment(T t) {
        counter.append(" ").append(t);
    }

    @Override
    public BinaryOperator<CollectAndStream<T, StringBuilder, String>> combiner() {
        return (left, right) -> new SpaceJoin<>(((SpaceJoin<T>) left).get(), ((SpaceJoin<T>) right).get());
    }

    public String get() {
        return counter.toString().trim();
    }

    @Override
    public Function<CollectAndStream<T, StringBuilder, String>, StringBuilder> finisher() {
        return x -> ((SpaceJoin<T>) x).counter;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return new HashSet<>();
    }
}
