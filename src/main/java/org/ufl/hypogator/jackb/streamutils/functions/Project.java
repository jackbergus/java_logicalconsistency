package org.ufl.hypogator.jackb.streamutils.functions;

import org.ufl.hypogator.jackb.streamutils.data.Tuple;
import org.ufl.hypogator.jackb.streamutils.utils.MapOperations;

import java.util.function.Function;

public class Project implements Function<Tuple, Tuple> {

    private final String[] args;

    public Project(String... args) {
        this.args = args;
    }

    @Override
    public Tuple apply(Tuple tuple) {
        return MapOperations.project(tuple, args);
    }

}
