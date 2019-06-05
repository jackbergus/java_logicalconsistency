package org.ufl.hypogator.jackb.m9;

import javafx.util.Pair;

import java.util.function.Function;

public abstract class Benchmark<I, O> implements Function<I, Pair<Double, O>> {

    public abstract O function(I input);

    @Override
    public Pair<Double, O> apply(I input) {
        long startTime = System.currentTimeMillis();
        O result = function(input);
        long endTime = System.currentTimeMillis();
        return new Pair<>(((endTime-startTime)*1.0)/1000.0, result);
    }

}
