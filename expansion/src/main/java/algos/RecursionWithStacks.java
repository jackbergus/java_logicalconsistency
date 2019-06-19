package algos;

import javafx.util.Pair;

import java.util.Optional;
import java.util.Stack;

public abstract class RecursionWithStacks<Status> {

    Stack<Pair<Status, Optional<Iterable<Status>>>> recursionStack;

    public RecursionWithStacks() {
        recursionStack = new Stack<>();
    }

    public void run(Status init) {
        recursionStack.push(new Pair<>(init, Optional.empty()));
        run();
    }

    protected abstract void afterRecursiveCall(Status prev, Iterable<Status> statuses);
    protected abstract Iterable<Status> beforeRecursiveCall(Status curr);

    private void run() {
        while (!recursionStack.isEmpty()) {
            Pair<Status, Optional<Iterable<Status>>> e = recursionStack.pop();
            if (e.getValue().isPresent()) {
                /*  do the stuff that was supposed to be done
        ...         when function called with e is returning and
        ...         after the function returns to parent
                 */
                afterRecursiveCall(e.getKey(), e.getValue().get());
            } else {
                /*
                do the stuff that was supposed to be done before
        ...     e is recursively called and at the beginning of the
        ...     function
                 */
                Iterable<Status> it = beforeRecursiveCall(e.getKey());
                // push e, so it would be visited again
                recursionStack.push(new Pair<>(e.getKey(), Optional.of(it)));
                // once all children are processed
                it.forEach(x -> recursionStack.push(new Pair<>(x, Optional.empty())));
            }
        }
    }
}
