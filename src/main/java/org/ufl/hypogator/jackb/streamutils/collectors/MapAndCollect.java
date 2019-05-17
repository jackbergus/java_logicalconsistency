package org.ufl.hypogator.jackb.streamutils.collectors;

import java.util.function.Function;
import java.util.function.Supplier;

public interface MapAndCollect<S, U, T> extends Function<S, T>, Supplier<U> {
}
