package org.ufl.hypogator.jackb.streamutils.iterator;

import java.util.Iterator;
import java.util.function.Function;

public interface IteratorOperators<T, K> extends Function<Iterator<T>, Iterator<K>> {
}
