package org.ufl.hypogator.jackb.streamutils.collectors;

import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.StreamSupport;

public abstract class CollectAndStream<SourceType, Collected, FinalType>
        implements Collector<SourceType, CollectAndStream<SourceType, Collected, FinalType>, Collected> {
    private final HashSet<Characteristics> features;

    /**
     * Maps the resulting type Collected into a stream of FinalType elements. This class implements the Java's
     * collector and uses an intermediate CollectAndStream as a collector for the intermediate computations within
     * the Collected type. A final data stream of FinalType will be returned if re-streamed, otherwise the Collected
     * element will be returned.
     *
     * @param input Collected to be mapped into a stream of FinalType-s
     * @return The stream of FinalType-s
     */
    protected abstract IteratorWithOperations<FinalType> restream(Collected input);

    private boolean doParallelize;

    /**
     * Sets whether we should parallelize the execution of our element or not.
     * @param doParallelize Default parallelization setting
     */
    public CollectAndStream(boolean doParallelize) {
        this.doParallelize = doParallelize;
        features = new HashSet<>();
        if (doParallelize) {
            features.add(Characteristics.CONCURRENT);
        }
        features.add(Characteristics.UNORDERED);
    }

    /**
     * Se the parallelization status for the current element
     * @param doParallelize
     * @return
     */
    public CollectAndStream<SourceType, Collected, FinalType> setParallelization(boolean doParallelize) {
        this.doParallelize = doParallelize;
        return this;
    }

    /**
     * Returns whether the current collector is setted to collect data in a parallel way
     * @return
     */
    public boolean isParallel() {
        return doParallelize;
    }

    /**
     * This class permits to use Java's streams only when I really need to parallelize and hence to do data replication or
     * performing "collect" operations. Otherwise, all the data is simply streamed by using iterators.
     *
     * @param source
     * @return
     */
    public Collected collect(Iterator<SourceType> source) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(source, Spliterator.IMMUTABLE), doParallelize)
                .collect(this);
    }

    /**
     * This methods mimics a groupBy operator, where the data is first collected and then restreamed.
     *
     * @param source
     * @return
     */
    public IteratorWithOperations<FinalType> group(Iterator<SourceType> source) {
        return restream(collect(source));
    }

    @Override
    public Set<Characteristics> characteristics() {
        return features;
    }

}
