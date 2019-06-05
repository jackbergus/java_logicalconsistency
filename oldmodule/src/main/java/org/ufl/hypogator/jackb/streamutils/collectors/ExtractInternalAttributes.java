package org.ufl.hypogator.jackb.streamutils.collectors;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.streamutils.data.Tuple;
import org.ufl.hypogator.jackb.streamutils.data.Value;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Collector used within the nested join for extracting the internal fields from a tuple. It stops after finding the
 * first shallow attribute -> value association
 */
public class ExtractInternalAttributes extends CollectAndStream<Tuple, HashMultimap<Tuple, Value>, Tuple> {

    final String field, tmpAggregators;
    private final HashSet<Characteristics> features;
    private HashMultimap<Tuple, Value> hmm = HashMultimap.create();

    /**
     * @param doParallelize  Parallelized execution
     * @param field          Field over which perform the extraction
     * @param tmpAggregators Temporarily field over which store the collected values (if any)
     */
    public ExtractInternalAttributes(boolean doParallelize, String field, String tmpAggregators) {
        super(doParallelize);
        this.field = field;
        this.tmpAggregators = tmpAggregators;
        features = new HashSet<>();
        if (doParallelize) {
            features.add(Characteristics.CONCURRENT);
        }
        features.add(Characteristics.UNORDERED);
    }

    /**
     * Nests the extracted values at the coarser level within the temporarily variable
     *
     * @param input Collected to be mapped into a stream of FinalType-s
     * @return
     */
    @Override
    protected IteratorWithOperations<Tuple> restream(HashMultimap<Tuple, Value> input) {
        final Iterator<Tuple> it = input.keySet().iterator();
        return new IteratorWithOperations<Tuple>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Tuple next() {
                Tuple t = it.next();
                HashSet<Value> hs = new HashSet<>();
                for (Value vs : input.get(t)) {
                    hs.addAll(vs.asList());
                }
                Tuple n = new Tuple();
                for (Value ts : hs) {
                    n.putStream(ts.getAtomAsString(), "");
                }
                return t.putStream(tmpAggregators, n.asValue());
            }
        };
    }

    @Override
    public Supplier<CollectAndStream<Tuple, HashMultimap<Tuple, Value>, Tuple>> supplier() {
        return () -> new ExtractInternalAttributes(isParallel(), field, tmpAggregators);
    }

    @Override
    public BiConsumer<CollectAndStream<Tuple, HashMultimap<Tuple, Value>, Tuple>, Tuple> accumulator() {
        return (x, t) -> ((ExtractInternalAttributes) x).put(t);
    }

    private void put(Tuple t) {
        hmm.put(t, t.extractFieldValues(field));
    }

    @Override
    public BinaryOperator<CollectAndStream<Tuple, HashMultimap<Tuple, Value>, Tuple>> combiner() {
        return (x1, x2) -> ((ExtractInternalAttributes) x1).putAll(((ExtractInternalAttributes) x2));
    }

    private CollectAndStream<Tuple, HashMultimap<Tuple, Value>, Tuple> putAll(ExtractInternalAttributes x2) {
        this.hmm.putAll(x2.hmm);
        return this;
    }

    @Override
    public Function<CollectAndStream<Tuple, HashMultimap<Tuple, Value>, Tuple>, HashMultimap<Tuple, Value>> finisher() {
        return x -> ((ExtractInternalAttributes) x).getInternal();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return features;
    }

    public HashMultimap<Tuple, Value> getInternal() {
        return hmm;
    }
}
