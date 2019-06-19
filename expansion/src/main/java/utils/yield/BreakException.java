package utils.yield;

public class BreakException extends RuntimeException {
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
