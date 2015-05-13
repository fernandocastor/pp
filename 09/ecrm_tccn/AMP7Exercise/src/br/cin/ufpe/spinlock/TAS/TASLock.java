package br.cin.ufpe.spinlock.TAS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import br.cin.ufpe.spinlock.BackOff.BackOff;

public class TASLock implements Lock {

	private AtomicBoolean state = new AtomicBoolean(false);

	// Implementation BackOff
	private static final int MIN_DELAY = 1;
	private static final int MAX_DELAY = 1000;

	@Override
	public void lock() {

		// Implementation BackOff
		BackOff backoff = new BackOff(MIN_DELAY, MAX_DELAY);

		while (state.getAndSet(true)) {
		}

		if (!state.getAndSet(true)) {
			return;
		} else {
			try { // Implementation BackOff
				backoff.backOffExponential();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void unlock() {
		state.set(false);

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