package org.ufl.hypogator.jackb.streamutils.functions;

import org.ufl.hypogator.jackb.streamutils.data.Tuple;

import java.util.function.Function;

public class Denomination implements Function<Tuple, Tuple> {


    private final String[] args;
    private final String as;

    public Denomination(String as, String... args) {
        this.args = args;
        this.as = as;
    }

    @Override
    public Tuple apply(Tuple tuple) {
        boolean isFound = false;
        for (String arg : args) {
            if (!isFound) {
                if (!tuple.isKeyEmpty(arg)) {
                    isFound = true;
                    tuple.copyAs(arg, as);
                }
            }
            tuple.remove(arg);
        }
        return tuple;
    }
}
