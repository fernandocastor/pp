package exercicio_91_TTASLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TTASLock implements Lock {
	AtomicBoolean state =new AtomicBoolean(false);
	public void lock() {
		while(true){
			if(!state.getAndSet(true)){
				return;
			}
			while(state.get()) {
			}
		}
	}
	public void unlock() {
		state.set(false);
	}
	
	private boolean isLocked() {
		return state.get();
	}
	
	@Override
	public void lockInterruptibly() throws InterruptedException {
	}
	@Override
	public Condition newCondition() {
		return null;
	}
	@Override
	public boolean tryLock() {
		return false;
	}
	@Override
	public boolean tryLock(long arg0, TimeUnit arg1)
			throws InterruptedException {
		return false;
	}
}
