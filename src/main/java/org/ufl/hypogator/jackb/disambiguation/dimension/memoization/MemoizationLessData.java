package org.ufl.hypogator.jackb.disambiguation.dimension.memoization;

import javafx.util.Pair;
import org.apache.jena.tdb.store.Hash;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.disambiguation.dimension.Direction;
import org.ufl.hypogator.jackb.utils.adt.HashMultimapSerializer;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Generalizes the memoization approach. Moreover, it helps not to memoize the reflexive relationships, and only focus
 * to the stored elements
 * @param <T>
 */
public class MemoizationLessData<T> {
    private boolean myResult;
    private Pair<T, T> cp;
    private PartialOrderComparison result;
    Map<Pair<T, T>, PartialOrderComparison> memoize2;

    public long getMemoizationSize() {
        return memoize2.size();
    }

    public MemoizationLessData() {
        memoize2 = new HashMap<>();
    }
//LRUCache<>(100, 100)
    public MemoizationLessData(File file) {
        memoize2 = HashMultimapSerializer.unserializeMap(file);
    }


    public boolean hasResult() {
        return myResult;
    }

    public Pair<T, T> getCp() {
        return cp;
    }

    public PartialOrderComparison getResult() {
        return result;
    }

    public PartialOrderComparison getResultAsPartialOrderComparison() {
        return result;
    }

    public void memoizeAsNone() {
        if (hasResult() || cp != null) {
            memoize2.put(cp, PartialOrderComparison.PERFECT_UNCOMPARABLE);
        }
    }

    public void memoizeAsNone(Pair<T, T> cp) {
        memoize2.put(cp, PartialOrderComparison.PERFECT_UNCOMPARABLE);
    }

    public void memoizeAs(Direction dir, double score, List<T> terms) {
        memoizeAs(new Pair<>(dir, Optional.of(new Pair<>(score, terms))));
    }

    public static <T> PartialOrderComparison fromVerbose(Pair<Direction, Optional<Pair<Double, List<T>>>> res) {
        return new PartialOrderComparison(res.getKey().toDirectionType(), res.getValue().isPresent() ? res.getValue().get().getKey() : 1.0);
    }

    public void memoizeAs(Pair<Direction, Optional<Pair<Double, List<T>>>> res) {
        if (hasResult() || cp != null) {
            memoize2.put(cp, new PartialOrderComparison(res.getKey().toDirectionType(), res.getValue().isPresent() ? res.getValue().get().getKey() : 1.0));
        }
    }

    public void memoizeAs(Pair<T, T> cp, Pair<Direction, Optional<Pair<Double, List<T>>>> res) {
        memoize2.put(cp, new PartialOrderComparison(res.getKey().toDirectionType(), res.getValue().isPresent() ? res.getValue().get().getKey() : 1.0));
    }

    public MemoizationLessData<T> invoke(T left, T right) {
        myResult = false;
        cp = new Pair<>(left, right);
        if (left.equals(right)) {
            result = PartialOrderComparison.PERFECT_EQUAL;
        } else {
            Pair<T, T> cpI = new Pair<>(right, left);
            PartialOrderComparison res = memoize2.get(cp);
            result = null;
            if (res != null)
                result = res;
            res = memoize2.get(cpI);
            if (res != null) {
                result = new PartialOrderComparison(res.t.invert(), res.uncertainty);
            }
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

    public void serializeToDisk(File file) {
        HashMultimapSerializer.serializeMap(memoize2, file);
    }

    public void memoizeAs(Pair<T, T> cp, POCType equal, double both) {
        memoizeAs(cp, new Pair<>(equal.asDirection(), equal.equals(POCType.Uncomparable) ? Optional.empty() : Optional.of(new Pair<>(both, Collections.emptyList()))));
    }

    public void loadFromDisk(File file) {
        memoize2 = HashMultimapSerializer.unserializeMap(file);
        if (memoize2 == null)
            memoize2 = new LRUCache<>(10, 10000);
    }

    public void appendFromDisk(File file) {
        Map<Pair<T, T>, PartialOrderComparison> unser = HashMultimapSerializer.unserializeMap(file);
        if (unser!= null)
            memoize2.putAll(unser);
    }
}
