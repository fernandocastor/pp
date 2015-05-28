package Q98;

import java.util.concurrent.CountDownLatch;

public class ActiveThread implements Runnable {

	private int id;
	private CountDownLatch doneSignal;

	ActiveThread(int id, CountDownLatch done) {
		this.id = id;
		doneSignal = done;
	}

	public void run() {
		doWork();
		System.out.println("Count Down: " + this.id);
		doneSignal.countDown();// notify driver we’re done
	}

	public void doWork() {
		int h = 0;
		for (int j = 0; j < Integer.MAX_VALUE; j++) {
			h = j;
		}
	}
	
}
