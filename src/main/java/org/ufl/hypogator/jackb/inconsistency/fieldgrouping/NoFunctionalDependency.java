package org.ufl.hypogator.jackb.inconsistency.fieldgrouping;

import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.inconsistency.AgileField;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.inconsistency.typecomparisonpolicy.FieldComparisonPolicy;

import java.util.HashSet;
import java.util.Set;

/**
 * This policy ignores the functional dependencies within the data, and just performs the comparison between all the
 * elements within it. Therefore,
 */
public class NoFunctionalDependency implements FieldGroupingPolicy {

    @Override
    public double fieldGroupingPolicy(AgileRecord left, AgileRecord right, HashSet<POCType> types) {
        int n = Integer.min(left.size(), right.size());
        FieldComparisonPolicy scoringPolicy = getFieldComparisonPolicy();
        double score = 1.0;
        for (int j = 0; j < n; j++) {
            Set<AgileField> vl = left.ith(j);
            Set<AgileField> ul = right.ith(j);
            score = score * (1.0 - scoringPolicy.getScore(left.schema.get(j), types, vl, ul));
        }
        return  1.0 - score;
    }

    @Override
    public String getName() {
        return "NoFunctionalDependency";
    }

    @Override
    public boolean doesPolicyNotRequireExtendedComparison() {
        return true;
    }
}
