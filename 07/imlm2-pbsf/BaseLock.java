

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


public abstract class BaseLock implements Lock {

	public abstract boolean isLocked();

	@Override
	public void lockInterruptibly() throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean tryLock() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit)
			throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Condition newCondition() {
		throw new UnsupportedOperationException();
	}
}
