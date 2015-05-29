package Q98;

import java.util.concurrent.CountDownLatch;

public class PassiveThread implements Runnable {
	private int id;
	private CountDownLatch startSignal;

	PassiveThread(int id, CountDownLatch start) {
		this.id = id;
		this.startSignal = start;
	}

	public void run() {
		try {
			startSignal.await();
			doWork();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void doWork() {
		System.out.println("Work Passive Thread:" + this.id);		
	}
	
	
}
