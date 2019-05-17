package org.ufl.hypogator.jackb.streamutils.utils;

import org.ufl.hypogator.jackb.streamutils.data.Tuple;
import org.ufl.hypogator.jackb.streamutils.data.Value;

import java.util.*;

public class MapOperations {

    /**
     * Given two maps having keys with the same type K, returns a new map having as keyset the keys in common from the
     * two maps, and associating k -> v1 from the first map to the k -> v2 from the second map through (v1 ->v2) in the
     * resulting map.
     *
     * @param left
     * @param right
     * @param <K>
     * @param <V1>
     * @param <V2>
     * @return
     */
    public static <K, V1, V2> HashMap<V1, V2> chain(Map<K, V1> left, Map<K, V2> right) {
        Set<K> commonKeys = new HashSet<>(left.keySet());
        commonKeys.retainAll(right.keySet());
        HashMap<V1, V2> toret = new HashMap<>();
        for (K key : commonKeys) {
            toret.put(left.get(key), right.get(key));
        }
        return toret;
    }


    public static <K, V> Map<K, V> disjointUnion(Map<K, V> left, Map<K, V> right) {
        if (left == null)
            return right;
        else if (right == null)
            return left;
        Set<K> commonKeys = new HashSet<>(left.keySet());
        Set<K> allKeys = new HashSet<>(left.keySet().size()+right.keySet().size());
        commonKeys.retainAll(right.keySet());
        allKeys.addAll(left.keySet());
        allKeys.addAll(right.keySet());
        HashMap<K, V> toRet = new HashMap<>();
        for (K key : allKeys) {
            V l = left.get(key);
            if (l == null) {
                toRet.put(key, right.get(key));
            } else {
                if (commonKeys.contains(key)) {
                    if (Objects.equals(l, right.get(key))) {
                        toRet.put(key, l);
                    } else {
                        return null;
                    }
                } else {
                    toRet.put(key, l);
                }
            }
        }
        return toRet;
    }


    public static <K, V> Map<K, V> project(Map<K, V> map, Set<K> fields) {
        HashMap<K, V> projected = new HashMap<>();
        for (Map.Entry<K, V> e : map.entrySet()) {
            if (fields.contains(e.getKey())) projected.put(e.getKey(), e.getValue());
        }
        return projected;
    }

    public static Tuple project(Tuple map, String... fieldArray) {
        HashSet<String> fields = new HashSet<>();
        for (String ignored : fieldArray)
            fields.add(ignored);
        return project(map, fields);
    }

    public static Tuple project(Tuple map, Set<String> fields) {
        Tuple projected = new Tuple();
        for (Map.Entry<String, Value> e : map.entrySet()) {
            if (fields.contains(e.getKey())) projected.put(e.getKey(), e.getValue());
        }
        return projected;
    }

    /**
     * Give two tuples, combines the two tuples preserving each other value
     *
     * @param left
     * @param right
     * @return
     */
    public static Tuple combine(Tuple left, Tuple right) {
        Tuple toReturn = new Tuple();
        HashSet<String> keys = new HashSet<>(left.size() + right.size());
        keys.addAll(left.keySet());
        keys.addAll(right.keySet());
        boolean isLeft, isRight;
        for (String key : keys) {
            Value v = null;
            isLeft = left.containsKey(key);
            isRight = right.containsKey(key);
            if (isLeft && isRight) {
                v = new Value(2);
                v.addValue(left.get(key));
                v.addValue(right.get(key));
            } else if (isLeft) {
                v = left.get(key);
            } else {
                v = right.get(key);
            }
            toReturn.put(key, v);
        }
        return toReturn;
    }

    public static <V1, V2> HashMap<V1, V2> chain(V1[] left, V2[] right) {
        int len = Integer.min(left.length, right.length);
        HashMap<V1, V2> toret = new HashMap<>();
        for (int i = 0; i < len; i++) {
            toret.put(left[i], right[i]);
        }
        return toret;
    }

    /**
     * Given a map representation of a finite function, performs the function composition of the two operands
     *
     * @param sourceFunction
     * @param targetFunction
     * @param <S>
     * @param <I>
     * @param <T>
     * @return targetFunction \circ sourceFunction
     */
    public static <S, I, T> HashMap<S, T> compose(Map<S, I> sourceFunction, Map<I, T> targetFunction) {
        HashMap<S, T> composed = new HashMap<>();
        for (Map.Entry<S, I> es : sourceFunction.entrySet()) {
            if (targetFunction.containsKey(es.getValue())) {
                composed.put(es.getKey(), targetFunction.get(es.getValue()));
            }
        }
        return composed;
    }

}
