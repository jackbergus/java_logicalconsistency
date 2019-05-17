package org.ufl.hypogator.jackb.streamutils.operations.joins;

import org.ufl.hypogator.jackb.streamutils.collectors.CollectAndStream;
import org.ufl.hypogator.jackb.streamutils.functions.TerFunction;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.function.BiFunction;

public class GenericRightJoinOperand<T1, T1Value, T2, FromT2Grouped, T2Grouped, Result> {
    private CollectAndStream<T2, T2Grouped, ?> group;
    private GenericLeftJoinOperand<T1, T1Value, T2, FromT2Grouped, T2Grouped, Result> builder;

    public GenericRightJoinOperand(GenericLeftJoinOperand<T1, T1Value, T2, FromT2Grouped, T2Grouped, Result> builder, IteratorWithOperations<T2> right, CollectAndStream<T2, T2Grouped, ?> group) {
        this.builder = builder;
        this.builder.rightOperand = right;
        this.group = group;
    }

    /**
     * @param rightSelector Select the grouped element from the right operand
     * @param joinCombiner  Function combining the left datum with the collection of the right elements that have been matched
     * @return
     */
    public IteratorWithOperations<Result> where(BiFunction<T2Grouped, T1Value, FromT2Grouped> rightSelector,
                                                TerFunction<T1, T1Value, FromT2Grouped, Result> joinCombiner) {
        final T2Grouped collected = builder.rightOperand.collect(group);
        System.out.println("WHERE is collected");
        return builder.leftOperand.map(((T1 t1) -> {
            T1Value t1E = builder.leftSelector.apply(t1);
            FromT2Grouped rightSelected = rightSelector.apply(collected, t1E);
            return joinCombiner.apply(t1, t1E, rightSelected);
        }));
    }
}