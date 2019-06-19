package utils.yield;

import java.util.Optional;

public interface YieldMessage<T> {
    Optional<T> value();
    static <T> YieldMessage<T> message(T value) {
        return () -> Optional.of(value);
    }
}
