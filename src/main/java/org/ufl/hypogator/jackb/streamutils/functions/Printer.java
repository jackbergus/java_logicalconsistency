package org.ufl.hypogator.jackb.streamutils.functions;

import java.util.function.Consumer;

public class Printer<T> implements Consumer<T> {
    @Override
    public void accept(T t) {
        System.out.println("\n" + t + "\n");
    }
}
