package org.ufl.hypogator.jackb.streamutils.operations.joins.nestedjoin;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.streamutils.collectors.TupleGroupBy;
import org.ufl.hypogator.jackb.streamutils.data.Tuple;
import org.ufl.hypogator.jackb.streamutils.data.Value;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;
import org.ufl.hypogator.jackb.streamutils.operations.joins.GenericLeftJoinOperand;
import org.ufl.hypogator.jackb.streamutils.utils.MapOperations;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class NestedLeft extends GenericLeftJoinOperand<Tuple, Tuple, Tuple, Tuple, HashMultimap<Tuple, Tuple>, Tuple> {
    private final String leftKey;
    private final String aggregatedKey;

    /**
     * @param leftOperand        Left operand over which perform the join
     * @param attributeLeftField Field selection of type T1Value over the left element from the left operand
     */
    public NestedLeft(IteratorWithOperations<Tuple> leftOperand,
                      String leftKey, String aggregatedKey,
                      Function<Tuple, Tuple> attributeLeftField) {
        super(leftOperand, attributeLeftField);
        this.leftKey = leftKey;
        this.aggregatedKey = aggregatedKey;
    }

    public IteratorWithOperations<Tuple> withNestingRight(IteratorWithOperations<Tuple> right, String gg, String mid) {
        return new NestedRight(this, right, TupleGroupBy.perform(true, gg, mid))
                .where((rightAggregated, value) -> {
                    for (String x : value.keySet()) {
                        Tuple t = new Tuple();
                        t.putStream(mid, x);
                        Set<Tuple> s = rightAggregated.get(t);
                        Tuple toPut = new Tuple();
                        for (Tuple ts : s) {
                            toPut = MapOperations.combine(toPut, ts);
                        }
                        value.put(x, toPut.asValue());
                    }
                    return value;
                }, (leftTupleWithLs, ignored, enrichedTupleWithRightValues) -> {
                    leftTupleWithLs.removeStream(aggregatedKey);
                    for (Map.Entry<String, Value> x : enrichedTupleWithRightValues.entrySet()) {
                        leftTupleWithLs.replaceWith(leftKey, new Value(x.getKey()), x.getValue()).expand(leftKey);
                    }
                    return leftTupleWithLs.removeStream(leftKey);
                });
    }
}
