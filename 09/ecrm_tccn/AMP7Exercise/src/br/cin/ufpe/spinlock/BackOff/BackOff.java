package br.cin.ufpe.spinlock.BackOff;

import java.util.Random;

public class BackOff {
	final int minDelay, maxDelay;
	int limit;
	final Random random;
	
	public BackOff(int min, int max) {
		minDelay = min;
		maxDelay = max;
		limit = minDelay;
		random = new Random();
	}
	
	public void backOffExponential() throws InterruptedException{
		int delay = random.nextInt(limit);
		limit = Math.min(maxDelay, 2 * limit);
		Thread.sleep(delay);
	}
	
	public void backoffAdditive() throws InterruptedException {
		int delay = random.nextInt(limit);
		limit = Math.min(maxDelay, 10 + limit);
		Thread.sleep(delay);
	}
}