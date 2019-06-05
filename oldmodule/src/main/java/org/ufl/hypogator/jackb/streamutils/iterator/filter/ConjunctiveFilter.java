package org.ufl.hypogator.jackb.streamutils.iterator.filter;

import org.ufl.hypogator.jackb.streamutils.data.Tuple;
import org.ufl.hypogator.jackb.streamutils.utils.UtilArrays;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class ConjunctiveFilter extends Filter<Tuple> {
    public List<FilterPredicate> predicateList;
    public List<Integer> element;

    /**
     * @param inConjunction Predicates to be selected
     */
    public ConjunctiveFilter(int... inConjunction) {
        predicateList = new ArrayList<>();
        element = new ArrayList<>(inConjunction.length);
        for (int in : inConjunction) {
            element.add(in);
        }
    }

    public ConjunctiveFilter(FilterPredicate... fp) {
        this(UtilArrays.range(0, fp.length - 1));
        for (FilterPredicate p : fp) {
            addPredicate(p);
        }
    }

    public ConjunctiveFilter addPredicate(FilterPredicate fp) {
        return addPredicate(false, fp);
    }

    public ConjunctiveFilter addPredicate(boolean toSelect, FilterPredicate fp) {
        if (toSelect) element.add(predicateList.size());
        predicateList.add(fp);
        return this;
    }

    public BitSet hashElement(Tuple t) {
        BitSet bs = new BitSet();
        for (int i = 0, predicateListSize = predicateList.size(); i < predicateListSize; i++) {
            FilterPredicate fp = predicateList.get(i);
            if (fp.test(t))
                bs.set(i);
        }
        return bs;
    }

    @Override
    public boolean test(Tuple tuple) {
        BitSet bs = hashElement(tuple);
        for (int i1 = 0, elementLength = element.size(); i1 < elementLength; i1++) {
            int i = element.get(i1);
            if (!bs.get(i)) return false;
        }
        return true;
    }
}
