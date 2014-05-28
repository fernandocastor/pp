package pp;

import java.util.Random;

public class Backoff {
	final int minDelay, maxDelay;
	int limit;
	final Random random;

	public Backoff(int min, int max) {
		minDelay = min;
		maxDelay = min;
		limit = minDelay;
		random = new Random();
	}

	public void backoff() throws InterruptedException {
		int delay = random.nextInt(limit);
		limit = Math.min(maxDelay, 2 * limit);// valor back off multiplicando.
		// limit = Math.min(maxDelay, limit + 40); // valor back off somando.
		Thread.sleep(delay);
	}
}
