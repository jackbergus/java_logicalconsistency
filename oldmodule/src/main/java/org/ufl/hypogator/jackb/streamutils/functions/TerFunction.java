package org.ufl.hypogator.jackb.streamutils.functions;

@FunctionalInterface
public interface TerFunction<T1, T2, T3, V> {
    V apply(T1 x1, T2 x2, T3 x3);
}
