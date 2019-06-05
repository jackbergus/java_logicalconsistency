package org.ufl.hypogator.jackb.inconsistency.typecomparisonpolicy;

import com.google.common.collect.TreeMultimap;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.inconsistency.AgileField;

public class MixtureModel implements FieldComparisonPolicy {

    private final static ScoreFieldRelyOnTA2Type sureTyping = new ScoreFieldRelyOnTA2Type();
    private final static BroadClassApproximation nothingIsSafe = new BroadClassApproximation();

    @Override
    public void argumentComparison(String fieldName, TreeMultimap<Double, POCType> map, AgileField left, AgileField right) {
        if (left.typeFromFuzzyMatch || right.typeFromFuzzyMatch) {
            sureTyping.argumentComparison(fieldName, map, left, right);
        } else {
            nothingIsSafe.argumentComparison(fieldName, map, left, right);
        }
    }

    @Override
    public String getName() {
        return "MixtureModel";
    }
}
