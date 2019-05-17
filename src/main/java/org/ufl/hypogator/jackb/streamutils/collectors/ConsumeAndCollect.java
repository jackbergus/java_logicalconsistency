package org.ufl.hypogator.jackb.streamutils.collectors;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ConsumeAndCollect<T, R> extends Consumer<T>, Supplier<R> {
}
