import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExactCounter {
	public static final int READERSLEEP = 100;
	public static final int NTHREADS = 8;
	public static final long COUNTERMAX = 4294967296L;

	private static final int CM_BITS = 16;
	private static final int MAX_COUNTERMAX = ((1 << CM_BITS) - 1);

	private Lock mutex = new ReentrantLock();
	private List<CounterThread> counters;
	private int nThreads;

	private long globalCountMax = 0;
	private long globalCount = 0;
	private long globalReserve = 0;

	private static class CounterAndMax {
		public AtomicInteger cam;

		public CounterAndMax() {
			cam = new AtomicInteger();
		}

		public SplittedCounterAndMax split() {
			return SplittedCounterAndMax.fromCounterAndMax(this);
		}

		public static int merge(int c, int cm) {
			long cami = (c << CM_BITS) | cm;

			return ((int) cami);
		}
	}

	private static class SplittedCounterAndMax {
		public int counter;
		public int counterMax;
		public int oldValue;

		private SplittedCounterAndMax(int c, int cm) {
			counter = c;
			counterMax = cm;
		}

		public static SplittedCounterAndMax fromCounterAndMax(CounterAndMax ctrAndMax) {
			int cami = ctrAndMax.cam.get();

			return SplittedCounterAndMax.fromInteger(cami);
		}

		public static SplittedCounterAndMax fromInteger(int cami) {
			int c = (cami >> CM_BITS) & MAX_COUNTERMAX;
			int cm = cami & MAX_COUNTERMAX;

			SplittedCounterAndMax scam = new SplittedCounterAndMax(c, cm);
			scam.oldValue = cami;
			return scam;
		}
	}

	private class CounterThread extends Thread {
		private CounterAndMax counterAndMax = new CounterAndMax();
		private volatile boolean running = false;

		public int getCounter() {
			return this.counterAndMax.split().counter;
		}

		public void stopRunning() {
			this.running = false;
		}

		private boolean addCountFastPath(long delta) {
			int oldValue;
			int newValue;

			do {
				SplittedCounterAndMax scam = counterAndMax.split();
				oldValue = scam.oldValue;
				if (delta > MAX_COUNTERMAX || scam.counter + delta  > scam.counterMax) {
					return false;
				}
				newValue = CounterAndMax.merge((int) (scam.counter + delta), scam.counterMax);
			} while(!counterAndMax.cam.compareAndSet(oldValue, newValue));

			return true;
		}

		private boolean addCount(long delta) {
			if (addCountFastPath(delta))
				return true;

			mutex.lock();
			globalizeCount();
			if (globalCountMax - globalCount - globalReserve < delta) {
				flushLocalCount();
				if (globalCountMax - globalCount - globalReserve < delta) {
					mutex.unlock();
					return false;
				}
			}

			globalCount += delta;
			balanceCount();
			mutex.unlock();
			return true;
		}

		private void globalizeCount() {
			SplittedCounterAndMax scam = counterAndMax.split();
			globalCount += scam.counter;
			globalReserve -= scam.counterMax;
			counterAndMax.cam.set(0);
		}

		private void flushLocalCount() {
			if (globalReserve == 0)
				return;

			for (CounterThread t : counters) {
				int old = t.counterAndMax.cam.getAndSet(0);
				SplittedCounterAndMax scam = SplittedCounterAndMax.fromInteger(old);
				globalCount += scam.counter;
				globalReserve -= scam.counterMax;
			}
		}

		private void balanceCount() {
			int c, cm, old;
			long limit;

			limit = globalCountMax - globalCount - globalReserve;
			limit /= nThreads;

			if (limit > MAX_COUNTERMAX)
				cm = MAX_COUNTERMAX;
			else
				cm = (int) limit;
			globalReserve += cm;
			c = 0;
			old = CounterAndMax.merge(c, cm);
			counterAndMax.cam.set(old);
		}

		@Override
		public void run() {
			while (this.running) {
				this.addCount(1);
			}
		}

		@Override
		public synchronized void start() {
			this.running = true;
			super.start();
		}
	}

	private class ReaderThread extends Thread {
		private long readCount() {
			mutex.lock();
			long sum = globalCount;
			for (CounterThread t : counters) {
				sum += t.getCounter();
			}
			mutex.unlock();
			
			return sum;
		}

		@Override
		public void run() {
			long sum;

			do {
				sum = readCount();

				System.out.println("Total counter: " + sum);

				try {
					Thread.sleep(READERSLEEP);
				} catch (InterruptedException e) {
					System.err.println("The reader thread was interrupted");
					e.printStackTrace();
					System.exit(1);
				}
			} while(sum < globalCountMax);

			for (CounterThread t : counters) {
				t.stopRunning();
			}
		}
	}

	public void execute(int nThreads, long counterMax) throws InterruptedException {
		System.out.println("# of threads: " + nThreads);
		System.out.println("Max. counter: " + counterMax);
		System.out.println();

		if (nThreads < 1 || counterMax < 1) {
			System.err.println("Invalid arguments");
			System.exit(1);
		}

		this.nThreads = nThreads;
		this.counters = new ArrayList<CounterThread>();
		this.globalCountMax = counterMax;

		for (int i = 0; i < nThreads; i++) {
			CounterThread t = new CounterThread();
			this.counters.add(t);
			t.start();
		}

		ReaderThread reader = new ReaderThread();
		reader.start();
		reader.join();
	}

	public static void call(int nThreads, long counterMax) throws InterruptedException {
		ExactCounter c = new ExactCounter();
		c.execute(nThreads, counterMax);
	}

	public static void call(String args[]) throws InterruptedException {
		if (args.length < 2) {
			System.err.println("Too few arguments");
			System.exit(1);
		}

		int nThreads = Integer.parseInt(args[0]);
		long counterMax = Long.parseLong(args[1]);

		ExactCounter.call(nThreads, counterMax);
	}

	public static void main(String args[]) throws InterruptedException {
		if (args.length == 2)
			ExactCounter.call(args);
		else
			ExactCounter.call(NTHREADS, COUNTERMAX);
	}
}
