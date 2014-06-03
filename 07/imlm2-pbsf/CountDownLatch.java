

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountDownLatch {

	private volatile int counter;
	private final Lock lock = new ReentrantLock();
	private final Condition zero = lock.newCondition();

	public CountDownLatch(int counter) {
		this.counter = counter;
	}

	public void countDown() {
		lock.lock();
		try {
			counter--;
			if(counter == 0) {
				zero.signalAll();
			}
		} finally {
			lock.unlock();
		}
	}

	public void await() {
		lock.lock();
		try {
			while(this.counter > 0) {
				try {
					zero.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} finally {
			lock.unlock();
		}
	}

	public static void main(String[] args) {
		int n = 10;
		CountDownLatch startSignal = new CountDownLatch(1);
		CountDownLatch doneSignal = new CountDownLatch(n);
		for (int i = 0; i < n; ++i) // start threads
			new Thread(new Worker(startSignal, doneSignal)).start();
		doSomethingElse(); // get ready for threads
		startSignal.countDown(); // unleash threads
		doSomethingElse(); // biding my time ...
		doneSignal.await(); // wait for threads to finish
	}

	private static void doSomethingElse() {
		System.out.println("Driver is done!");
	}

	static class Worker implements Runnable {
		private final CountDownLatch startSignal, doneSignal;
		Worker(CountDownLatch myStartSignal, CountDownLatch myDoneSignal) {
			startSignal = myStartSignal;
			doneSignal = myDoneSignal;
		}
		public void run() {
			startSignal.await(); // wait for driver’s OK to start
			doWork();
			doneSignal.countDown(); // notify driver we’re done
		}
		private void doWork() {
			long tid = Thread.currentThread().getId();
			System.out.println(tid + " did some work");
		}
	}
}
