package org.ufl.hypogator.jackb.streamutils.iterators;

import org.ufl.hypogator.jackb.streamutils.collectors.CollectAndStream;
import org.ufl.hypogator.jackb.streamutils.collectors.FlatMap;
import org.ufl.hypogator.jackb.streamutils.collectors.Sink;
import org.ufl.hypogator.jackb.streamutils.iterators.operations.FilteredIterator;
import org.ufl.hypogator.jackb.streamutils.iterators.operations.MapIterator;
import org.ufl.hypogator.jackb.streamutils.operations.joins.GenericLeftJoinOperand;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface IteratorWithOperations<T> extends Iterator<T> {

    default Iterable<T> asIterable() {
        return () -> this;
    }

    default IteratorWithOperations<T> filter(Predicate<T> predicate) {
        return new FilteredIterator<>(this, predicate);
    }

    default <K> IteratorWithOperations<K> map(Function<T, K> map) {
        return new MapIterator<>(this, map);
    }

    default <Metadata> MetadataIterator<Metadata, T> extractMetadata(Function<Iterator<T>, Metadata> extractor) {
        return new MetadataIterator<>(extractor.apply(this), this);
    }

    default IteratorWithOperations<T> yield(Consumer<T> forEach) {
        return map(x -> {
            forEach.accept(x);
            return x;
        });
    }

    default <K, F> K collect(CollectAndStream<T, K, F> collector) {
        return collector.collect(this);
    }

    default <K, F> IteratorWithOperations<F> collectAndRestream(CollectAndStream<T, K, F> collector) {
        return collector.group(this);
    }

    default <T1Value, T2, FromT2Grouped, T2Grouped, Result> GenericLeftJoinOperand<T, T1Value, T2, FromT2Grouped, T2Grouped, Result> coGroupWithLeftSelector(Function<T, T1Value> attributeLeftField) {
        return new GenericLeftJoinOperand<>(this, attributeLeftField);
    }

    default void sink(boolean parallelize) {
        collect(new Sink<>(parallelize));
    }

    default <K> IteratorWithOperations<K> flatMap(Function<T, IteratorWithOperations<K>> map) {
        return flatMap(map, true);
    }

    default <K> IteratorWithOperations<K> flatMap(Function<T, IteratorWithOperations<K>> map, boolean parallelize) {
        return collectAndRestream(new FlatMap<>(parallelize, map));
    }
}
