import java.util.concurrent.atomic.AtomicLong;

public class CounterThread extends Thread {

	private volatile boolean mRun = true;
	private final AtomicLong mCounterAndMax;
	private final ReaderThread mReader;
	final static long MAX_COUNTERMAX = (1l << 32) - 1;

	public CounterThread(ReaderThread reader) {
		mCounterAndMax = new AtomicLong(0);
		mReader = reader;
	}

	public long[] splitCounterAndMax(long value) {
		return new long[] { (value >> 32) & MAX_COUNTERMAX,
				value & MAX_COUNTERMAX };
	}

	public long mergeCounterAndMax(long counter, long max) {
		return ((counter << 32) | max);
	}

	public void setCounterAndMax(long value) {
		mCounterAndMax.set(value);
	}

	public long getAndSetCounter(long value) {
		return mCounterAndMax.getAndSet(value);
	}

	public void increment() {
		long[] counterAndMax;
		long n;
		long oldValue;
		boolean fastPath = false;
		do {
			oldValue = mCounterAndMax.get();
			counterAndMax = splitCounterAndMax(oldValue);
			if (counterAndMax[0] + 1 > counterAndMax[1])
				break;
			n = mergeCounterAndMax(counterAndMax[0] + 1, counterAndMax[1]);
			fastPath = true;
		} while (!mCounterAndMax.compareAndSet(oldValue, n));
		if (fastPath)
			return;

		synchronized (mReader) {
			globalizeCount();
			if (mReader.getGlobalCountMax() - mReader.getGlobalCount()
					- mReader.getGlobalReserve() < 1) {
				mReader.flushThreadCount();
				if (mReader.getGlobalCountMax() - mReader.getGlobalCount()
						- mReader.getGlobalReserve() < 1) {
					return;
				}
			}
			mReader.addToGlobalCount(1);
			balanceCount();
		}
	}

	private void globalizeCount() {
		long[] counterAndMax = splitCounterAndMax(mCounterAndMax.get());
		mReader.addToGlobalCount(counterAndMax[0]);
		mReader.subtractFromGlobalReserve(counterAndMax[1]);
		mCounterAndMax.set(0);
	}

	private void balanceCount() {
		long counter;
		long counterMax;
		long limit = mReader.getGlobalCountMax() - mReader.getGlobalCount()
				- mReader.getGlobalReserve();

		limit /= mReader.numberOfThreads();
		if (limit > MAX_COUNTERMAX) {
			counterMax = MAX_COUNTERMAX;
		} else {
			counterMax = limit;
		}
		mReader.addToGlobalReserve(counterMax);
		counter = 0;
		mCounterAndMax.set(mergeCounterAndMax(counter, counterMax));
	}

	public long getSum() {
		long value = mCounterAndMax.get();
		return (value >> 32) & MAX_COUNTERMAX;
	}

	public void stopCounter() {
		mRun = false;
	}

	@Override
	public void run() {
		while (mRun) {
			increment();
		}
	}
}
