public class IntCounterThread extends CounterThread {

	public IntCounterThread(boolean volatile_) {
		super(volatile_);
	}

	private int mCounter;
	private volatile int mVolatileCounter;

	@Override
	public void increment() {
		if (mVolatile)
			mVolatileCounter++;
		else
			mCounter++;
	}

	@Override
	public long getSum() {
		if (mVolatile)
			return mVolatileCounter;

		return mCounter;
	}
}
