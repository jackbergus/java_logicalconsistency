package utils.yield;

import java.util.Optional;

import static utils.yield.YieldExceptions.unchecked;


public interface Yielderable<T> extends Iterable<T> {

    void execute(YieldDefinition<T> builder);

    default ClosableIterator<T> iterator() {
        YieldDefinition<T> yieldDefinition = new YieldDefinition<>();
        Thread collectorThread = new Thread(() -> {
            yieldDefinition.waitUntilFirstValueRequested();
            try {
                execute(yieldDefinition);
            } catch (BreakException e) {
            }
            yieldDefinition.signalComplete();
        });
        collectorThread.setDaemon(true);
        collectorThread.start();
        yieldDefinition.onClose(collectorThread::interrupt);
        return yieldDefinition.iterator();
    }
}


