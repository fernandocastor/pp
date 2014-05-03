package T04;

import java.util.concurrent.CountDownLatch;

public class ThreadCount extends Thread {

	private int cont = 0;

	public Boolean stopFlag;
	final CountDownLatch firstGate;

	public ThreadCount(Boolean stopFlag, CountDownLatch firstGate) {
		super();
		this.stopFlag = stopFlag;
		this.firstGate = firstGate;
	}

	public int getCont() {
		return cont;
	}

	@Override
	public void run() {
		try {
			firstGate.await();
			while (stopFlag) {
				cont++;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setStopFlag(Boolean stopFlag) {
		this.stopFlag = stopFlag;
	}
}
