import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class SimpleReadWriteLock implements ReadWriteLock {
	int readers;
	boolean writer;
	Lock lock;
	Object monitor;
	Lock readLock, writeLock;

	public SimpleReadWriteLock() {
		writer = false;
		readers = 0;
		lock = new ReentrantLock();
		readLock = new ReadLock();
		writeLock = new WriteLock();
		monitor = this;
	}

	public Lock readLock() {
		return readLock;
	}

	public Lock writeLock() {
		return writeLock;
	}

	private class ReadLock implements Lock {
		public void lock() {
			synchronized (monitor) {
				while (writer) {
					try {
						monitor.wait();
					} catch (Exception e) {}
				}
				readers++;
			}
		}

		public void unlock() {
			synchronized (monitor) {
				readers--;
				if (readers == 0)
					monitor.notifyAll();
			}
		}

	    @Override
	    public void lockInterruptibly() throws InterruptedException {
	    }

	    @Override
	    public boolean tryLock() {
	        return false;
	    }

	    @Override
	    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
	        return false;
	    }

	    @Override
	    public Condition newCondition() {
	        return null;
	    }
	}

	private class WriteLock implements Lock {
		public void lock() {
			synchronized (monitor) {
				while (readers > 0) {
					try {
						monitor.wait();
					} catch (Exception e) {}
				}
				writer = true;
			}
		}

		public void unlock() {
			synchronized (monitor) {
				writer = false;
				monitor.notifyAll();
			}
		}

	    @Override
	    public void lockInterruptibly() throws InterruptedException {
	    }

	    @Override
	    public boolean tryLock() {
	        return false;
	    }

	    @Override
	    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
	        return false;
	    }

	    @Override
	    public Condition newCondition() {
	        return null;
	    }
	}
}