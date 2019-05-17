package org.ufl.hypogator.jackb.streamutils.functions;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A currified bi-function is a lazy-evaluated function, that can be executed by passing the provided arguments
 * one element per time
 * @param <X1>      First argument's type
 * @param <X2>      Second argument's type
 * @param <Y>       Result type
 */
public class CurrifiedBiFunction<X1, X2, Y> implements Function<X1, Function<X2, Y>> {
    private BiFunction<X1, X2, Y> bifun;
    private SecondArgument firstArgument;

    public CurrifiedBiFunction(BiFunction<X1, X2, Y> bifun) {
        this.bifun = bifun;
        this.firstArgument = new SecondArgument();
    }

    /**
     * The currification of the bi-function returns a function that "stores" the first arguments, and evaluates the
     * remaining ones.
     *
     * @param x1 First function's argument
     * @return
     */
    @Override
    public Function<X2, Y> apply(X1 x1) {
        return firstArgument.setFirstArgument(x1);
    }

    /**
     * Actual class implementing the currification
     */
    private class SecondArgument implements Function<X2, Y> {
        /**
         * First applied argument
         */
        private X1 first;

        /**
         * This constructor accepts the first argument applied to a function. Its instantiation returns a simple function
         * that
         * @param argument
         * @return          Currified function where the first argument is already applied
         */
        private SecondArgument setFirstArgument(X1 argument) {
            this.first = argument;
            return this;
        }

        /**
         *
         * @param x2        Second and last function
         * @return          Result returned from the associated bi-function
         */
        @Override
        public Y apply(X2 x2) {
            /**
             * Now, the bi function accepts both arguments at the same time
             */
            return bifun.apply(first, x2);
        }
    }
}
