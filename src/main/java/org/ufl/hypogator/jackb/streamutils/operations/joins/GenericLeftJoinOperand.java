package org.ufl.hypogator.jackb.streamutils.operations.joins;

import org.ufl.hypogator.jackb.streamutils.collectors.CollectAndStream;
import org.ufl.hypogator.jackb.streamutils.collectors.ExtractInternalAttributes;
import org.ufl.hypogator.jackb.streamutils.data.Tuple;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;
import org.ufl.hypogator.jackb.streamutils.operations.joins.cogroupedjoin.CoGroupedLeft;
import org.ufl.hypogator.jackb.streamutils.operations.joins.nestedjoin.NestedLeft;

import java.util.function.Function;

/**
 * @param <T1>            Type of the elements from the left
 * @param <T1Value>       Value extracted from a key from the left
 * @param <T2>            Type fo the elements from the right
 * @param <FromT2Grouped>
 * @param <T2Grouped>
 * @param <Result>
 */
public class GenericLeftJoinOperand<T1, T1Value, T2, FromT2Grouped, T2Grouped, Result> {
    public final IteratorWithOperations<T1> leftOperand;
    public final Function<T1, T1Value> leftSelector;
    public IteratorWithOperations<T2> rightOperand;

    /**
     * @param leftOperand        Left operand over which perform the join
     * @param attributeLeftField Field selection of type T1Value over the left element from the left operand
     */
    public GenericLeftJoinOperand(IteratorWithOperations<T1> leftOperand, Function<T1, T1Value> attributeLeftField) {
        this.leftOperand = leftOperand;
        this.leftSelector = attributeLeftField;
    }

    public static CoGroupedLeft joinLeftTuples
            (IteratorWithOperations<Tuple> leftOperand, String leftSelector) {
        return new CoGroupedLeft(leftOperand, leftSelector, tp -> tp.get(leftSelector).getAtomAsString());
    }

    /**
     * @param right Right operand over which perform the join
     * @param group Group by element acting as the left cogrouping and bucketing
     * @return
     */
    public GenericRightJoinOperand<T1, T1Value, T2, FromT2Grouped, T2Grouped, Result> withGropuedRight
    (IteratorWithOperations<T2> right, CollectAndStream<T2, T2Grouped, ?> group) {
        return new GenericRightJoinOperand<>(this, right, group);
    }

    public static NestedLeft nestedJoinLeftTuples(IteratorWithOperations<Tuple> left2, String arg_id, String ls) {
        return new NestedLeft(left2.collectAndRestream(new ExtractInternalAttributes(true, arg_id, ls)),
                arg_id, ls,
                tp -> tp.get(ls).getAtom().asTuple());
    }
}
