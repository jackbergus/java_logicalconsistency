package org.ufl.hypogator.jackb.m9.producer_consumer;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class PProducer<Token> implements Runnable {

	private final BlockingQueue<Token> blockingQueue;
	private final Iterator<Token> tokenIterator;

	PProducer(BlockingQueue<Token> blockingQueue, Iterator<Token> tokenIterator) {
		this.blockingQueue = blockingQueue;
		this.tokenIterator = tokenIterator;
	}

	@Override
	public void run() {
		while (tokenIterator.hasNext()) {
			Token token = tokenIterator.next();
			/**
			 * Insert the token element in the Queue. Wait if no space is
			 * available
			 */
			try {
				System.out.println("New token issued :" + token);
				blockingQueue.put(token);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}