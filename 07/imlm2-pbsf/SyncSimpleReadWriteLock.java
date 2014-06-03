

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;


public class SyncSimpleReadWriteLock implements ReadWriteLock {


	private volatile int readers;
	private volatile boolean writer;
	private final Object monitor;
	private final SyncReadLock readLock;
	private final SynWriteLock writeLock;

	public SyncSimpleReadWriteLock() {
		this.monitor = new Object();
		this.readLock = new SyncReadLock();
		this.writeLock = new SynWriteLock();
	}

	@Override
	public Lock readLock() {
		return readLock;
	}

	@Override
	public Lock writeLock() {
		return writeLock;
	}

	private class SyncReadLock implements Lock {

		@Override
		public void lock() {
			synchronized (monitor) {
				while(writer) {
					try {
						monitor.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                }
                readers++;
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
		public void unlock() {
			synchronized (monitor) {
				readers--;
				if(readers == 0) {
					monitor.notifyAll();
				}
			}
		}

		@Override
		public Condition newCondition() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private class SynWriteLock implements Lock {

		@Override
		public void lock() {
			synchronized (monitor) {
				while(readers > 0 || writer) {
					try {
						monitor.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				writer = true;
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
		public void unlock() {
			synchronized (monitor) {
				writer = false;
				monitor.notifyAll();
			}
		}

		@Override
		public Condition newCondition() {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
