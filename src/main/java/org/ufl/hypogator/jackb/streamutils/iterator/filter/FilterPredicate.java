package org.ufl.hypogator.jackb.streamutils.iterator.filter;

import org.ufl.hypogator.jackb.streamutils.data.Tuple;

import java.util.function.Predicate;

public abstract class FilterPredicate implements Predicate<Tuple> {
    public int hashCode(Tuple t) {
        return test(t) ? 1 : 0;
    }
}
