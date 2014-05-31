import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class SimpleReadWriteLock implements ReadWriteLock {
	int readers;
	boolean writer;
	Lock readLock, writeLock;

	public SimpleReadWriteLock() {
		writer = false;
		readers = 0;
		readLock = new ReadLock();
		writeLock = new WriteLock();
	}

	public Lock readLock() {
		return readLock;
	}

	public Lock writeLock() {
		return writeLock;
	}

	class ReadLock implements Lock {
		public void lock() {
			synchronized (SimpleReadWriteLock.this) {
				try {
					while (writer) {
						SimpleReadWriteLock.this.wait();
					}
					readers++;
				} catch (Exception e) {}
			}
		}

		public void unlock() {
			synchronized (SimpleReadWriteLock.this) {
				try {
					readers--;
					if (readers == 0)
						SimpleReadWriteLock.this.notifyAll();
				} finally {}
			}
		}

		@Override
		public Condition newCondition() {
			// TODO Auto-generated method stub
			return null;
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
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			// TODO Auto-generated method stub
			return false;
		}
	}

	protected class WriteLock implements Lock {
		public void lock() {
			synchronized (SimpleReadWriteLock.this) {
				try {
					while (readers > 0 || writer) {
						SimpleReadWriteLock.this.wait();
					}
					writer = true;
				} catch (Exception e) {}
			}
		}

		public void unlock() {
			synchronized (SimpleReadWriteLock.this) {
				try {
					writer = false;
					SimpleReadWriteLock.this.notifyAll();
				} finally {}
			}
		}

		@Override
		public Condition newCondition() {
			// TODO Auto-generated method stub
			return null;
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
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			// TODO Auto-generated method stub
			return false;
		}
	}
}