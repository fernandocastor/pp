

import java.util.concurrent.atomic.AtomicBoolean;

public class TASLock extends BaseLock {

	private final AtomicBoolean state;

	public TASLock() {
		this.state = new AtomicBoolean(false);
	}

	@Override
	public void lock() {
		while(true) {
			while(state.get());
			if(!state.getAndSet(true)) {
				return;
			}
		}
	}

	@Override
	public void unlock() {
		state.set(false);
	}

	@Override
	public boolean isLocked() {
		return !state.get();
	}

}
