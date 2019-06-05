package org.ufl.hypogator.jackb.streamutils.operations.joins.cogroupedjoin;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.streamutils.collectors.TupleGroupBy;
import org.ufl.hypogator.jackb.streamutils.data.Tuple;
import org.ufl.hypogator.jackb.streamutils.data.Value;
import org.ufl.hypogator.jackb.streamutils.functions.TerFunction;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;
import org.ufl.hypogator.jackb.streamutils.operations.joins.GenericLeftJoinOperand;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CoGroupedLeft extends GenericLeftJoinOperand<Tuple, String, Tuple, Value, HashMultimap<Tuple, Tuple>, Tuple> {

    private String leftField;

    /**
     * @param leftOperand        Left operand over which perform the join
     * @param attributeLeftField Field selection of type T1Value over the left element from the left operand
     * @param x                  Left field over which the join is performed
     */
    public CoGroupedLeft(IteratorWithOperations<Tuple> leftOperand, String x, Function<Tuple, String> attributeLeftField) {
        super(leftOperand, attributeLeftField);
        leftField = x;
    }

    /**
     *
     * @param ter
     * @param right         Right operand
     * @param content
     * @param mid
     * @return
     */
    public IteratorWithOperations<Tuple> withGroupedRight(TerFunction<Tuple, String, Value, Tuple> ter, IteratorWithOperations<Tuple> right, String content, String mid) {
        return new CoGroupedRight(this, right, leftField, TupleGroupBy.perform(true, content, mid))
                .where(new ActualLeftCoGrouping(mid),
                       (leftTuple, i, x3) -> ter.apply(leftTuple, leftField, x3)
                );
    }

    /**
     *
     * @param right         Right operand
     * @param content
     * @param mid
     * @return
     */
    public IteratorWithOperations<Tuple> withTupleGropuedRight(IteratorWithOperations<Tuple> right, String content, String mid) {
        return withGroupedRight(Tuple::putStream, right, content, mid);
    }

    private static class ActualLeftCoGrouping implements BiFunction<HashMultimap<Tuple, Tuple>, String, Value> {
        private String mid;

        public ActualLeftCoGrouping(String mid) {
            this.mid = mid;
        }

        public Value apply(HashMultimap<Tuple, Tuple> rightAggregated, String leftValue) {
            Set<Tuple> ls;
            {
                Tuple t = new Tuple();
                t.put(mid, new Value(leftValue));
                ls = rightAggregated.get(t);
            }
            Value v = new Value(ls.size());
            for (Tuple t : ls) {
                v.addValue(t);
            }
            return v;
        }
    }
}
