package org.ufl.hypogator.jackb.streamutils.functions;

import java.util.function.BiConsumer;

public class EmptyConsumer<T, U> implements BiConsumer<T, U> {

    private static EmptyConsumer self = new EmptyConsumer();

    private EmptyConsumer() {
    }

    public static <L, R> EmptyConsumer<L, R> instance() {
        return (EmptyConsumer<L, R>) self;
    }

    @Override
    public void accept(T t, U u) {

    }
}
