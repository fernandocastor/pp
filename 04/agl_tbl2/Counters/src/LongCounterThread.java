public class LongCounterThread extends CounterThread {

	public LongCounterThread(boolean volatile_) {
		super(volatile_);
	}

	private long mCounter;
	private volatile long mVolatileCounter;

	@Override
	public void increment() {
		if (mVolatile)
			mVolatileCounter++;
		else
			mCounter++;
	}

	public long getSum() {
		if (mVolatile)
			return mVolatileCounter;

		return mCounter;
	}
}
