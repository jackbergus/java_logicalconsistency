package org.ufl.hypogator.jackb.inconsistency.typecomparisonpolicy;

import com.google.common.collect.TreeMultimap;
import org.ufl.hypogator.jackb.comparators.partialOrders.InformationPreservingComparator;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.disambiguation.disambiguationFromKB;
import org.ufl.hypogator.jackb.inconsistency.AgileField;
import org.ufl.hypogator.jackb.inconsistency.legacy.TupleComparator;

import java.util.Objects;

/**
 * This class relies on the type assoicated by the TA2 elements. Therefore, the information of all the possible types
 * associated to the TA2 is not provided.
 *
 * Therefore, the associated dimension is ignored.
 */
public class ScoreFieldRelyOnTA2Type implements FieldComparisonPolicy {
    private disambiguationFromKB expandedKBBaseline;


    /*@Override
    public Double getScore(String fieldName, HashSet<PartialOrderComparison.Type> fieldType, Set<AgileField> vl, Set<AgileField> ul) {
        TreeMultimap<Double, PartialOrderComparison.Type> map = TreeMultimap.create();
        for (AgileField v : vl) {
            for (AgileField u : ul) {
                argumentComparison(fieldName, map, v, u);
            }
        }
        double MAX = map.keySet().last();
        NavigableSet<PartialOrderComparison.Type> typesFromMax = map.get(MAX);
        if (typesFromMax.contains(PartialOrderComparison.Type.Lesser) &&
                typesFromMax.contains(PartialOrderComparison.Type.Greater)) {
            fieldType.add(PartialOrderComparison.Type.Equal);
            //return new PartialOrderComparison(PartialOrderComparison.Type.Equal, 1.0 - score);
        } else if (typesFromMax.contains(PartialOrderComparison.Type.Lesser)) {
            //return new PartialOrderComparison(PartialOrderComparison.Type.Lesser, score);
            fieldType.add(PartialOrderComparison.Type.Lesser);
        } else if (typesFromMax.contains(PartialOrderComparison.Type.Greater)) {
            //return new PartialOrderComparison(PartialOrderComparison.Type.Greater, score);
            fieldType.add(PartialOrderComparison.Type.Greater);
        } else if (typesFromMax.contains(PartialOrderComparison.Type.Equal)) {
            //return new PartialOrderComparison(PartialOrderComparison.Type.Equal, score);
            fieldType.add(PartialOrderComparison.Type.Equal);
        } else {
            fieldType.add(PartialOrderComparison.Type.Uncomparable);
        }
        return MAX;
    }*/

    @Override
    public void argumentComparison(String fieldName, TreeMultimap<Double, POCType> map, AgileField left, AgileField right) {
        // Since I'm dealing with conflicting types,
        // The best predictor for which dimension to use so far is the label
        PartialOrderComparison cmp1;
        POCType t1 = null;
        double u1 = 0.0;
        if (left.fieldName != null) {
            InformationPreservingComparator<String> cmp = TupleComparator.generateFromTypeAndField(fieldName, left.fieldType);
            if (cmp == null) {
                if (left.fieldString.equals(right.fieldString))
                    cmp1 = PartialOrderComparison.PERFECT_EQUAL;
                else
                    cmp1 = PartialOrderComparison.PERFECT_UNCOMPARABLE;
            } else {
                cmp1 = cmp.compare(left.fieldString, right.fieldString);
            }
            t1 = cmp1.t;
            u1 = cmp1.uncertainty;
        }

        PartialOrderComparison cmp2;
        POCType t2 = null;
        double u2 = 0.0;
        if (right.fieldName != null) {
            //cmp2 = TupleComparator.generateFromType(u.fieldName).compare(v.fieldString, u.fieldString);
            InformationPreservingComparator<String> cmp = TupleComparator.generateFromTypeAndField(fieldName, right.fieldType);
            if (cmp == null) {
                if (left.fieldString.equals(right.fieldString))
                    cmp2 = PartialOrderComparison.PERFECT_EQUAL;
                else
                    cmp2 = PartialOrderComparison.PERFECT_UNCOMPARABLE;
            } else {
                cmp2 = cmp.compare(left.fieldString, right.fieldString);
            }
            t2 = cmp2.t;
            u2 = cmp2.uncertainty;
        }

        // If both directions return incomparable, everything is uncomparable
        if (Objects.equals(t1, POCType.Uncomparable) &&
                Objects.equals(t2, POCType.Uncomparable))
            map.put((1 - (1 - u1) * (1 - u2)), POCType.Uncomparable);
            //return new PartialOrderComparison(PartialOrderComparison.Type.Uncomparable,  (1-(1-u1)*(1-u2))); // 1)
        else if (Objects.equals(t1, POCType.Uncomparable)) {
            map.put(u2, t2);
                /*types.add(t1);
                score = score * (((1-u1)));*/
        } else if (Objects.equals(t2, POCType.Uncomparable)) {
            map.put(u1, t1);
                /*types.add(t2);
                score = score * (((1-u2)));*/
        } else

            // Both directions provide the same type, then ihe score is the conjunction of the two scores
            if (t1 != null && Objects.equals(t1, t2)) {
                map.put((((u1) + (u2) - u1 * u2)), t1);
                /*types.add(cmp1.t);
                score = score * (1-(((u1)+(u2)-u1*u2)));*/


            } else
                // Both directions are identified: therefore, I have an equivalence
                if (t1 != null && t2 != null && !Objects.equals(t1, t2)) {
                    map.put((((u1) + (u2) - u1 * u2)) * 0.9, POCType.Equal);
                /*score = score * (1-((u1)+(u2)-u1*u2)*0.9);
                types.add(PartialOrderComparison.Type.Equal);*/
                }
    }

    @Override
    public String getName() {
        return "ScoreFieldRelyOnTA2Type";
    }

    /*@Override
    public void setExpandedKBBaseline(disambiguationFromKB expandedKBBaseline) {
        this.expandedKBBaseline = expandedKBBaseline;
        TupleComparator.setGlobalExpandedKBBaseline(expandedKBBaseline);
    }*/
}
