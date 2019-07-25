package org.ufl.hypogator.jackb.streamutils.iterator.filter;

import org.ufl.hypogator.jackb.streamutils.data.Tuple;

import java.util.ArrayList;
import java.util.List;

public class DisjunctiveFilter extends Filter<Tuple> {

    public List<ConjunctiveFilter> chain;

    public DisjunctiveFilter(ConjunctiveFilter... chains) {
        chain = new ArrayList<>(chains.length);
        for (int i = 0; i < chains.length; i++) {
            chain.add(chains[i]);
        }
    }

    public DisjunctiveFilter addConjunctivePredicate(ConjunctiveFilter pred) {
        chain.add(pred);
        return this;
    }

    @Override
    public boolean test(Tuple tuple) {
        for (int i = 0, chainSize = chain.size(); i < chainSize; i++) {
            ConjunctiveFilter fdpc = chain.get(i);
            if (fdpc.test(tuple)) return true;
        }
        return false;
    }
}
