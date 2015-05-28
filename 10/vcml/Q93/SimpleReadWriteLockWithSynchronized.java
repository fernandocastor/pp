package Q93;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SimpleReadWriteLockWithSynchronized implements IReadWriteLock {

	private int readers;
	private boolean writer;
	private Lock readLock;
	private Lock writeLock;
	private Object objLock;

	public SimpleReadWriteLockWithSynchronized() {
		writer = false;
		readers = 0;
		readLock = new ReadLock();
		writeLock = new WriteLock();
		objLock = this;
	}

	@Override
	public Lock readLock() {
		return readLock;
	}

	@Override
	public Lock writeLock() {
		return writeLock;
	}

	class ReadLock implements Lock {

		@Override
		public void lock() {
			synchronized (objLock) {
				while (writer) {
					try {
						objLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				readers++;
			}
		}

		@Override
		public void unlock() {
			synchronized (objLock) {
				readers--;
				if (readers == 0) {
					objLock.notifyAll();
				}
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
		public boolean tryLock(long time, TimeUnit unit)
				throws InterruptedException {
			return false;
		}

		@Override
		public Condition newCondition() {
			return null;
		}
	}

	protected class WriteLock implements Lock {

		@Override
		public void lock() {
			synchronized (objLock) {
				while (readers > 0 || writer) {
					try {
						objLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					writer = true;
				}
			}
		}

		@Override
		public void unlock() {
			synchronized (objLock) {
				writer = false;
				objLock.notifyAll();
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
		public boolean tryLock(long time, TimeUnit unit)
				throws InterruptedException {
			return false;
		}

		@Override
		public Condition newCondition() {
			return null;
		}
	}

}
