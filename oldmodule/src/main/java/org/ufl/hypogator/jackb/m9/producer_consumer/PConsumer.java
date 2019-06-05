package org.ufl.hypogator.jackb.m9.producer_consumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class PConsumer<Token> implements Callable<Long> {

	private BlockingQueue<Token> blockingQueue;
	private CountDownLatch countDownLatch;
	private Consumer<Token> tokenConsumer;

	PConsumer(BlockingQueue<Token> blockingQueue, Consumer<Token> tokenConsumer, CountDownLatch cdl) {
		this.blockingQueue = blockingQueue;
		this.countDownLatch = cdl;
		this.tokenConsumer = tokenConsumer;
	}

	@Override
	public Long call() {
		/**
		 * Serving token one by one in a infinite loop.
		 * The Loop will break while there are no more
		 * token to serve
		 */
		while (true) {
			if (countDownLatch.getCount() == 0) {
				break;
			}
			try {
				// Serving the customer with the token
				Token token = blockingQueue.take();
				tokenConsumer.accept(token);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				countDownLatch.countDown();
			}
		}
		return 0L;
	}
}