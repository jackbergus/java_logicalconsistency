package org.ufl.hypogator.jackb.streamutils.operations.joins.cogroupedjoin;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.streamutils.collectors.CollectAndStream;
import org.ufl.hypogator.jackb.streamutils.data.Tuple;
import org.ufl.hypogator.jackb.streamutils.data.Value;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;
import org.ufl.hypogator.jackb.streamutils.operations.joins.GenericRightJoinOperand;

public class CoGroupedRight extends GenericRightJoinOperand<Tuple, String, Tuple, Value, HashMultimap<Tuple, Tuple>, Tuple> {

    private String leftField;

    public CoGroupedRight(CoGroupedLeft builder,
                          IteratorWithOperations<Tuple> right,
                          String leftField,
                          CollectAndStream<Tuple, HashMultimap<Tuple, Tuple>, ?> group) {
        super(builder, right, group);
        this.leftField = leftField;
    }

}