package org.ufl.hypogator.jackb.comparators.partialOrders.policy;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;

import java.util.ArrayList;
import java.util.Collection;

public interface DisambiguationPolicy {

    static POCType typeAggregator(Collection<POCType> aggregator) {
        if (aggregator == null || aggregator.isEmpty())
            return POCType.Uncomparable;
        else {
            int n = aggregator.size();
            if (n == 1)
                return aggregator.iterator().next();
            if (n == 4)
                return POCType.Equal;
            else {
                if (aggregator.contains(POCType.Uncomparable)) {
                    ArrayList<POCType> al = new ArrayList<>(aggregator);
                    al.remove(POCType.Uncomparable);
                    return typeAggregator(al);
                } else if (aggregator.contains(POCType.Equal)) {
                    ArrayList<POCType> al = new ArrayList<>(aggregator);
                    al.remove(POCType.Equal);
                    return typeAggregator(al);
                } else /*if (aggregator.contains(PartialOrderComparison.Type.Lesser) &&
                        aggregator.contains(PartialOrderComparison.Type.Greater))*/ {
                    ArrayList<POCType> al = new ArrayList<>(aggregator);
                    al.remove(POCType.Lesser);
                    al.remove(POCType.Greater);
                    return typeAggregator(al);
                }
            }
        }
    }

    PartialOrderComparison getDirection(HashMultimap<POCType, Double> map);
    String getName();
}
