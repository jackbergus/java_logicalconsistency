package org.ufl.hypogator.jackb.streamutils.operations.joins.nestedjoin;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.streamutils.collectors.CollectAndStream;
import org.ufl.hypogator.jackb.streamutils.data.Tuple;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;
import org.ufl.hypogator.jackb.streamutils.operations.joins.GenericRightJoinOperand;

public class NestedRight extends GenericRightJoinOperand<Tuple, Tuple, Tuple, Tuple, HashMultimap<Tuple, Tuple>, Tuple> {
    public NestedRight(NestedLeft builder,
                       IteratorWithOperations<Tuple> right,
                       CollectAndStream<Tuple, HashMultimap<Tuple, Tuple>, ?> group) {
        super(builder, right, group);
    }
}
