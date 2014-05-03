public class DoubleCounterThread extends CounterThread {

	public DoubleCounterThread(boolean volatile_) {
		super(volatile_);
	}

	private double mCounter;
	private volatile double mVolatileCounter;

	@Override
	public void increment() {
		if (mVolatile)
			mVolatileCounter++;
		else
			mCounter++;
	}

	public long getSum() {
		if (mVolatile)
			return (long) mVolatileCounter;

		return (long) mCounter;
	}
}
