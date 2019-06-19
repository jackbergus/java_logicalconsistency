package algos;

import utils.yield.YieldDefinition;
import utils.yield.Yielderable;

/**
 * Implements a non-tail recursive call, where the result is returned while it is computed via Yield. Therefore, the
 * class will act as a recursive method, where the constructor accepts the argument starting the recursive call. The
 * expected result will be provided by the
 * @param <Status>
 * @param <Result>
 */
public abstract class YielderRecursionWithStacks<Status, Result> extends  RecursionWithStacks<Status> implements Yielderable<Result> {

    private YieldDefinition<Result> yielder;
    private final Status initCall;

    public YielderRecursionWithStacks(Status initCall) {
        this.initCall = initCall;
    }

    protected YieldDefinition<Result> yield() {
        return yielder;
    }

    @Override
    public void execute(YieldDefinition<Result> builder) {
        this.yielder = builder;
        run(initCall);
    }
}
