public class CountDownLatch {
	int counter;

	public CountDownLatch(int n) {
		counter = n;
	}

	public synchronized void countDown() {
		counter--;

		if (counter == 0)
			notifyAll();
	}

	public synchronized void await() throws InterruptedException {
		while (counter > 0)
			wait();
	}
}