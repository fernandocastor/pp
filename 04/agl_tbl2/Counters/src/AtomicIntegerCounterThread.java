import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerCounterThread extends CounterThread {

	private AtomicInteger mCounter;
	private volatile AtomicInteger mVolatileCounter;

	public AtomicIntegerCounterThread(boolean volatile_) {
		super(volatile_);
		if (mVolatile)
			mVolatileCounter = new AtomicInteger();
		else
			mCounter = new AtomicInteger();
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
