package org.ufl.hypogator.jackb.disambiguation.dimension.memoization;

import com.google.common.collect.Lists;
import javafx.util.Pair;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.disambiguation.dimension.Direction;

import java.util.*;

/**
 * Generalizes the memoization approach. Moreover, it helps not to memoize the reflexive relationships, and only focus
 * to the stored elements
 * @param <T>
 */
public class MemoizationGeneralizer<T> {
    private boolean myResult;
    private Pair<T, T> cp;
    private Pair<Direction, Optional<Pair<Double, List<T>>>> result;
    HashMap<Pair<T, T>, Pair<Direction, Optional<Pair<Double, List<T>>>>> memoize2;

    public MemoizationGeneralizer() {
        memoize2 = new HashMap<>();
    }

    public boolean hasResult() {
        return myResult;
    }

    public Pair<T, T> getCp() {
        return cp;
    }

    public Pair<Direction, Optional<Pair<Double, List<T>>>> getResult() {
        return result;
    }

    public PartialOrderComparison getResultAsPartialOrderComparison() {
        return new PartialOrderComparison(result.getKey().toDirectionType() , result.getValue().isPresent() ? result.getValue().get().getKey() : 0.0);
    }

    public void memoizeAsNone() {
        if (hasResult()) {
            memoize2.put(cp, new Pair<>(Direction.NONE, Optional.empty()));
        }
    }

    public void memoizeAs(Direction dir, double score, List<T> terms) {
        memoizeAs(new Pair<>(dir, Optional.of(new Pair<>(score, terms))));
    }

    public void memoizeAs(Pair<Direction, Optional<Pair<Double, List<T>>>> res) {
        if (hasResult()) {
            memoize2.put(cp, res);
        }
    }

    public MemoizationGeneralizer<T> invoke(T left, T right) {
        myResult = false;
        cp = new Pair<>(left, right);
        Pair<T, T> cpI = new Pair<>(right, left);
        Pair<Direction, Optional<Pair<Double, List<T>>>> res = memoize2.get(cp);
        result = null;
        if (res != null)
            result = res;
        res = memoize2.get(cpI);
        if (res != null) {
            Optional<Pair<Double, List<T>>> opt = res.getValue(), optRet = null;
            optRet = opt.map(doubleListPair -> new Pair<>(doubleListPair.getKey(), Lists.reverse(doubleListPair.getValue())));
            result = new Pair<>(res.getKey().reverse(), optRet);
        }
        if (left.equals(right)) {
            res = new Pair<>(Direction.BOTH, Optional.of(new Pair<>(1.0, new ArrayList<>())));
            memoize2.put(cp, res);
            //memoize2.put(new Pair<>(right, left), res);
            result = res;
        }
        if (result != null) {
            myResult = true;
            return this;
        }
        myResult = false;
        return this;
    }

    public void memoizeAs(POCType equal, double both) {
        memoizeAs(new Pair<>(equal.asDirection(), equal.equals(POCType.Uncomparable) ? Optional.empty() : Optional.of(new Pair<>(both, Collections.emptyList()))));
    }
}
