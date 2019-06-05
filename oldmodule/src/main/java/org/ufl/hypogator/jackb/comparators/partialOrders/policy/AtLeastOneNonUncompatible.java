package org.ufl.hypogator.jackb.comparators.partialOrders.policy;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;

import java.util.Set;

import static org.ufl.hypogator.jackb.comparators.partialOrders.POCType.*;

public class AtLeastOneNonUncompatible implements DisambiguationPolicy {

    double threshold = ConfigurationEntrypoint.getInstance().threshold;

    private static double getMapDisjunction(POCType key, HashMultimap<POCType, Double> map, double defaultValue) {
        Set<Double> values = map.get(key);
        if (values.isEmpty())
            return defaultValue;
        return 1.0-map.get(key).stream().mapToDouble(x -> 1.0-x).reduce(1.0, (a, b) -> a * b);
    }

    @Override
    public PartialOrderComparison getDirection(HashMultimap<POCType, Double> map) {
        boolean hasComparison = false;
        if (map.containsKey(Lesser) && map.containsKey(Equal)) {
            map.putAll(Lesser, map.get(Equal));
            hasComparison = true;
        }
        if (map.containsKey(Greater) && map.containsKey(Equal)) {
            map.putAll(Greater, map.get(Equal));
            hasComparison = true;
        }
        if (map.containsKey(Equal) && hasComparison) {
            map.removeAll(Equal);
        }

        double greater = getMapDisjunction(Greater, map, -1);
        double lesser = getMapDisjunction(Lesser, map, -1);

        if (greater >=0 || lesser >= 0) {

            // If I have both lesser and greater scores
            if ((greater > 0 && lesser > 0) &&
                    // and if those are met with the threshold
                    (Math.abs(greater - lesser) >= threshold)) {
                return new PartialOrderComparison(Equal, greater + lesser - greater * lesser);
            } else {
                // Otherwise, return the only element or the one with the maximum score
                double max = Double.max(greater, lesser);
                if (max == greater)
                    return new PartialOrderComparison(Greater, greater);
                else
                    return new PartialOrderComparison(Lesser, lesser);
            }

        } else {
            // if neither greater and lesser are there, then the element is either equal or lesser
            if (map.containsKey(Equal)) {
                return new PartialOrderComparison(Equal, getMapDisjunction(Equal, map, 1.0));
            } else {
                return new PartialOrderComparison(Uncomparable, getMapDisjunction(Uncomparable, map, 1.0));
            }
        }
    }

    @Override
    public String getName() {
        return "AtLeastOneNonUncompatible";
    }
}
