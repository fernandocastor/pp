public class FloatCounterThread extends CounterThread {

	public FloatCounterThread(boolean volatile_) {
		super(volatile_);
	}

	private float mCounter;
	private volatile float mVolatileCounter;

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
			return (long) mVolatileCounter;

		return (long) mCounter;
	}
}
