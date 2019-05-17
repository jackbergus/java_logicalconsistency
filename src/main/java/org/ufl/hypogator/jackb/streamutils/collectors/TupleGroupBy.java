package org.ufl.hypogator.jackb.streamutils.collectors;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.streamutils.data.Tuple;
import org.ufl.hypogator.jackb.streamutils.data.Value;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;
import org.ufl.hypogator.jackb.streamutils.utils.MapOperations;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public class TupleGroupBy extends CollectAndStream<Tuple, HashMultimap<Tuple, Tuple>, Tuple> {

    private final Set<String> fixeds;
    private final HashMultimap<Tuple, Tuple> internal;
    private final String aggregated;

    protected TupleGroupBy(boolean doParallelize, Set<String> fixeds, String aggregated) {
        super(doParallelize);
        this.fixeds = fixeds;
        this.aggregated = aggregated;
        if (fixeds.contains(aggregated))
            throw new RuntimeException("Error: the aggregated field cannot have the same name of one of the grouped fields");
        this.internal = HashMultimap.create();
    }

    /**
     * @param doParallelize    Determines if the aggregation has to be performed in parallel or not
     * @param aggregated       Attribute containing the tuples matching with the given argument
     * @param equivalenceClass Attributes over which perform the group by
     * @return
     */
    public static TupleGroupBy perform(boolean doParallelize, String aggregated, String... equivalenceClass) {
        Set<String> hashSet = new HashSet<>();
        for (String x : equivalenceClass) {
            hashSet.add(x);
        }
        return new TupleGroupBy(doParallelize, hashSet, aggregated);
    }

    public HashMultimap<Tuple, Tuple> getInternal() {
        return internal;
    }

    /*

    @Override
    public BiConsumer<TupleGroupBy, Tuple> accumulator() {
        return TupleGroupBy::put;
    }*/

    private void put(Tuple tuple) {
        Tuple equivalenceClass = MapOperations.project(tuple, fixeds);
        Set<String> remainingKeys = new HashSet<>(tuple.keySet());
        remainingKeys.removeAll(fixeds);
        internal.put(equivalenceClass, MapOperations.project(tuple, remainingKeys));
    }

    /*@Override
    public BinaryOperator<TupleGroupBy> combiner() {
        return TupleGroupBy::putAll;
    }*/

    private TupleGroupBy putAll(TupleGroupBy right) {
        internal.putAll(right.getInternal());
        return this;
    }

    /*@Override
    public Function<TupleGroupBy, HashMultimap<Tuple, Tuple>> finisher() {
        return TupleGroupBy::getInternal;
    }*/

    @Override
    public Supplier<CollectAndStream<Tuple, HashMultimap<Tuple, Tuple>, Tuple>> supplier() {
        return () -> new TupleGroupBy(isParallel(), fixeds, aggregated);
    }

    @Override
    public BiConsumer<CollectAndStream<Tuple, HashMultimap<Tuple, Tuple>, Tuple>, Tuple> accumulator() {
        return (x, t) -> ((TupleGroupBy) x).put(t);
    }

    @Override
    public BinaryOperator<CollectAndStream<Tuple, HashMultimap<Tuple, Tuple>, Tuple>> combiner() {
        return (x1, x2) -> ((TupleGroupBy) x1).putAll(((TupleGroupBy) x2));
    }

    @Override
    public Function<CollectAndStream<Tuple, HashMultimap<Tuple, Tuple>, Tuple>, HashMultimap<Tuple, Tuple>> finisher() {
        return x -> ((TupleGroupBy) x).getInternal();
    }

    @Override
    public IteratorWithOperations<Tuple> restream(HashMultimap<Tuple, Tuple> input) {
        System.out.println("Restreaming now TupleGroupBy");
        final Iterator<Tuple> it = input.keySet().iterator();
        return new IteratorWithOperations<Tuple>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Tuple next() {
                Tuple cp = it.next();
                Set<Tuple> ls = input.get(cp);
                Value arrayValues = new Value(ls.size());
                for (Tuple t : ls) {
                    arrayValues.addValue(t);
                }
                cp.put(aggregated, arrayValues);
                return cp;
            }
        };
    }
}
