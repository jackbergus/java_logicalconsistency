package org.ufl.hypogator.jackb.inconsistency.typecomparisonpolicy;

import com.google.common.collect.TreeMultimap;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.inconsistency.AgileField;

import java.util.Objects;

/**
 * Performs some equality approximation between all the arguments
 */
public class EqualityApproximation implements FieldComparisonPolicy  {
    @Override
    public void argumentComparison(String fieldName, TreeMultimap<Double, POCType> map, AgileField left, AgileField right) {
        // Since I'm dealing with conflicting types,
        // The best predictor for which dimension to use so far is the label
        PartialOrderComparison cmp1;
        POCType t1 = null;
        double u1 = 0.0;
        if (Objects.equals(left.fieldString, right.fieldString))
            cmp1 = PartialOrderComparison.PERFECT_EQUAL;
        else
            cmp1 = PartialOrderComparison.PERFECT_UNCOMPARABLE;
        t1 = cmp1.t;
        u1 = cmp1.uncertainty;
        map.put(u1, t1);
    }

    @Override
    public String getName() {
        return "EqualityApproximation";
    }
}
