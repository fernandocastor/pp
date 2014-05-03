import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongCounterThread extends CounterThread {

	private AtomicLong mCounter;
	private volatile AtomicLong mVolatileCounter;

	public AtomicLongCounterThread(boolean volatile_) {
		super(volatile_);
		if (mVolatile)
			mVolatileCounter = new AtomicLong();
		else
			mCounter = new AtomicLong();
	}

	@Override
	public void increment() {
		if (mVolatile)
			mVolatileCounter.incrementAndGet();
		else
			mCounter.incrementAndGet();
	}

	@Override
	public long getSum() {
		if (mVolatile)
			return mVolatileCounter.get();

		return mCounter.get();
	}
}
