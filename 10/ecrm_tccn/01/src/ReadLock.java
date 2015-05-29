import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ReadLock implements Lock {

	private Object monitor;
	private int readers;
	private boolean writer;
	
	public ReadLock() {
		this.readers = 0;
		this.writer = false;
		this.monitor = new Object();
	}

	@Override
	public void lock() {
		synchronized (monitor) {
			try {
//				System.out.println("writer: " + writer);
				while (writer) {
					monitor.wait();
				}
				readers++;
			} catch (InterruptedException exception) {
				exception.printStackTrace();
			}
		}
	}

	@Override
	public void unlock() {
		synchronized (monitor) {
		 readers--;
//		 System.out.println("readers: " + readers);
		 if (readers == 0)
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