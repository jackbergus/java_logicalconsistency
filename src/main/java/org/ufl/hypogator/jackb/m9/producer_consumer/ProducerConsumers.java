package org.ufl.hypogator.jackb.m9.producer_consumer;

import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ProducerConsumers<Token> {

	BlockingQueue<Token> blockingQueue;
	int cores;

	public ProducerConsumers() {
		cores = Runtime.getRuntime().availableProcessors();
		cores = cores <= 1 ? 1 : cores-1;
		blockingQueue = new LinkedBlockingQueue<>();
	}

	public void parallelize(Iterator<Token> iterator, Consumer<Token> consumer, int sizeIterator) {
		blockingQueue.clear();
		PProducer<Token> tokenVendingMachine = new PProducer<>(blockingQueue, iterator);
		new Thread(tokenVendingMachine).start();
		CountDownLatch countDownLatch = new CountDownLatch(sizeIterator);
		ExecutorService executor = Executors.newFixedThreadPool(cores);

		PConsumer<Token> tokenConsumer = new PConsumer<>(blockingQueue, consumer, countDownLatch);
		for (int i = 1; i <= 5; i++) {
			executor.submit(tokenConsumer);
		}
		try {
			countDownLatch.await();
			System.out.println("Stopped");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}
	}
}