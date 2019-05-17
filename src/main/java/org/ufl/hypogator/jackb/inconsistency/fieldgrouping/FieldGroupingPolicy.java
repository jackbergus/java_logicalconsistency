package org.ufl.hypogator.jackb.inconsistency.fieldgrouping;

import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.inconsistency.typecomparisonpolicy.FieldComparisonPolicy;
import org.ufl.hypogator.jackb.inconsistency.typecomparisonpolicy.FieldComparisonPolicyFactory;

import java.util.HashSet;

public interface FieldGroupingPolicy {

    /**
     * Comparison between the two element using the given policy
     * @param left
     * @param right
     * @param types
     * @return
     */
    double fieldGroupingPolicy(AgileRecord left, AgileRecord right, HashSet<POCType> types);

    /**
     * Name associated to the policy
     * @return
     */
    String getName();

    /**
     * Checks whether we have to perform the comparison between all the possible subtuples having the same schema or
     * limiting to the elements that have the same schema in common.
     *
     * @return
     */
    boolean doesPolicyNotRequireExtendedComparison();

    default FieldComparisonPolicy getFieldComparisonPolicy() {
        return FieldComparisonPolicyFactory.getInstance().getPolicy(ConfigurationEntrypoint.getInstance().typingPolicy);
    }
}
