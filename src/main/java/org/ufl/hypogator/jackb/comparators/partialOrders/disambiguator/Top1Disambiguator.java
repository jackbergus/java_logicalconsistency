package org.ufl.hypogator.jackb.comparators.partialOrders.disambiguator;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.comparators.partialOrders.InformationPreservingComparator;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.disambiguation.DisambiguatedValue;
import org.ufl.hypogator.jackb.disambiguation.Resolved;
import org.ufl.hypogator.jackb.utils.adt.Triple;

public class Top1Disambiguator<T extends Resolved, K extends DisambiguatedValue<T>> extends AbstractDisambiguationsComparator<T, K> {
    /**
     * This class accepts a dimension for each element that can be disambiguated
     *
     * @param ambiguousComparator Comparator over ambiguous terms
     */
    public Top1Disambiguator(InformationPreservingComparator<T> ambiguousComparator) {
        super(ambiguousComparator);
    }

    @Override
    public HashMultimap<POCType, Double> apply(K left, K right) {
        HashMultimap<POCType, Double> map = HashMultimap.create();
        if ((!left.getDisambiguation().isEmpty()) && (!right.getDisambiguation().isEmpty())) {
            Triple<String, T, Double> le = left.getDisambiguation().iterator().next();
            Triple<String, T, Double> re = right.getDisambiguation().iterator().next();
            double leS = Double.min(le.third, 1.0);
            double reS = Double.min(re.third, 1.0);
            double leSOrR = (leS * reS);
            PartialOrderComparison cmp = ambiguousComparator.compare(le.second, re.second);
            //if (cmp.t != PartialOrderComparison.Type.Uncomparable)
            map.put(cmp.t, cmp.uncertainty * leSOrR);
        }
        return map;
    }
}
