package utils.yield;

import java.util.Optional;

public interface YieldCompleted<T> extends YieldMessage<T> {
    static <T> YieldCompleted<T> completed() { return () -> Optional.empty(); }
}
