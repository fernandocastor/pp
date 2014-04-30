import java.util.ArrayList;

public class ReaderThread extends Thread {

	private ArrayList<CounterThread> mCounters;
	private long mGlobalCount;
	private long mGlobalReserve;
	private final long mGlobalCountMax;

	public ReaderThread(long limit) {
		mGlobalCountMax = limit;
	}

	public long getGlobalCount() {
		return mGlobalCount;
	}

	public long getGlobalReserve() {
		return mGlobalReserve;
	}

	public long getGlobalCountMax() {
		return mGlobalCountMax;
	}

	public void subtractFromGlobalCount(long value) {
		mGlobalCount -= value;
	}

	public void addToGlobalCount(long value) {
		mGlobalCount += value;
	}

	public void subtractFromGlobalReserve(long value) {
		mGlobalReserve -= value;
	}

	public void addToGlobalReserve(long value) {
		mGlobalReserve += value;
	}

	public void setThreads(ArrayList<CounterThread> threads) {
		mCounters = threads;
	}

	private void stopThreads() {
		for (CounterThread thread : mCounters)
			thread.stopCounter();
	}

	public int numberOfThreads() {
		return mCounters.size();
	}

	public void flushThreadCount() {
		if (mGlobalReserve == 0)
			return;

		for (CounterThread thread : mCounters) {
			long counterAndMax = thread.getAndSetCounter(0);
			long counter = (counterAndMax >> 32) & CounterThread.MAX_COUNTERMAX;
			long max = counterAndMax & CounterThread.MAX_COUNTERMAX;
			mGlobalCount += counter;
			mGlobalReserve -= max;
		}
	}

	@Override
	public void run() {
		long sum = 0;
		do {
			try {
				sleep(1);
				synchronized (this) {
					sum = mGlobalCount;
					for (CounterThread counter : mCounters) {
						sum += counter.getSum();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (sum < mGlobalCountMax);
		System.out.println("Sum: " + sum);
		stopThreads();
	}
}
