import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class WriteLock implements Lock {

	private Object monitor;
	private int readers;
	private boolean writer;

	public WriteLock() {

		this.readers = 0;
		this.writer = false;
		this.monitor = new Object();
	}

	@Override
	public void lock() {
		synchronized (monitor) {
			try {
//				System.out.println("readers: " + readers + " | " + "writer: " + writer);
				while (readers > 0 || writer) {
					monitor.wait();
				}
				writer = true;
			} catch (InterruptedException exception) {
				exception.printStackTrace();
			}
		}
	}

	@Override
	public void unlock() {
		synchronized (monitor) {
//			System.out.println("writer: " + writer);
		 writer = false;
		 monitor.notifyAll();
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean tryLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit)
			throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}
}