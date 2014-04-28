import java.util.ArrayList;

public class ReaderThread extends Thread {

	private final ArrayList<CounterThread> mCounters;
	private long mSum;
	private final long mLimit;

	public ReaderThread(ArrayList<CounterThread> counters, long limit) {
		mCounters = counters;
		mLimit = limit;
	}

	private void stopThreads() {
		for (CounterThread thread : mCounters)
			thread.stopCounter();
	}

	@Override
	public void run() {
		do {
			try {
				sleep(1);
				printAggregatedSum();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (mSum < mLimit);
		stopThreads();
	}

	protected void printAggregatedSum() {
		for (CounterThread counter : mCounters) {
			mSum += counter.getSum();
		}
		System.out.println("Sum: " + mSum);
	}
}
