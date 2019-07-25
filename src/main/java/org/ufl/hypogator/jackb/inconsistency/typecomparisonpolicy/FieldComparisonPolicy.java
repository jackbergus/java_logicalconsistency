package org.ufl.hypogator.jackb.inconsistency.typecomparisonpolicy;

import com.google.common.collect.TreeMultimap;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.inconsistency.AgileField;

import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;

public interface FieldComparisonPolicy {

    /**
     * Given the arguments associated to both the left and right part, it returns the confidence score associated to
     * the current elements
     *
     * @param fieldName    Dimension associated to both the fields in vl and ul
     * @param fieldsTypes  Set where the information about the order of each element is set.
     * @param vl           Elements coming from the left part
     * @param ul           Elements coming from the right part
     * @return             The confidence score associated to the current field
     */
    default Double getScore(String fieldName, HashSet<POCType> fieldsTypes, Set<AgileField> vl, Set<AgileField> ul) {
        TreeMultimap<Double, POCType> map = TreeMultimap.create();
        for (AgileField v : vl) {
            for (AgileField u : ul) {
                argumentComparison(fieldName, map, v, u);
            }
        }
        double MAX = map.keySet().last();
        NavigableSet<POCType> typesFromMax = map.get(MAX);
        if (typesFromMax.contains(POCType.Lesser) &&
                typesFromMax.contains(POCType.Greater)) {
            fieldsTypes.add(POCType.Equal);
            //return new PartialOrderComparison(PartialOrderComparison.Type.Equal, 1.0 - score);
        } else if (typesFromMax.contains(POCType.Lesser)) {
            //return new PartialOrderComparison(PartialOrderComparison.Type.Lesser, score);
            fieldsTypes.add(POCType.Lesser);
        } else if (typesFromMax.contains(POCType.Greater)) {
            //return new PartialOrderComparison(PartialOrderComparison.Type.Greater, score);
            fieldsTypes.add(POCType.Greater);
        } else if (typesFromMax.contains(POCType.Equal)) {
            //return new PartialOrderComparison(PartialOrderComparison.Type.Equal, score);
            fieldsTypes.add(POCType.Equal);
        } else {
            fieldsTypes.add(POCType.Uncomparable);
        }
        return MAX;
    }

    void argumentComparison(String fieldName, TreeMultimap<Double, POCType> map, AgileField left, AgileField right);

    /**
     * Returns...
     * @return the name associated to the policy
     */
    String getName();
}
