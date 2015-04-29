import java.util.ArrayList;
import java.util.List;

public class StatisticalCounter {
	public static final int READERSLEEP = 100;
	public static final int NTHREADS = 8;
	public static final long COUNTERMAX = 4294967296L;

	private static class CounterThread extends Thread {
		private int counter;
		private volatile boolean running = false;

		public int getCounter() {
			return this.counter;
		}

		public void stopRunning() {
			this.running = false;
		}

		@Override
		public void run() {
			while (this.running) {
				this.counter++;
			}
		}

		@Override
		public synchronized void start() {
			this.running = true;
			super.start();
		}
	}

	private static class ReaderThread extends Thread {
		private List<CounterThread> counters;
		private long counterMax;

		public ReaderThread(List<CounterThread> counters, long counterMax) {
			this.counters = counters;
			this.counterMax = counterMax;
		}

		@Override
		public void run() {
			long sum;

			do {
				sum = 0;
				for (CounterThread t : counters) {
					sum += t.getCounter();
				}

				System.out.println("Total counter: " + sum);

				try {
					Thread.sleep(READERSLEEP);
				} catch (InterruptedException e) {
					System.err.println("The reader thread was interrupted");
					e.printStackTrace();
					System.exit(1);
				}
			} while(sum < this.counterMax);

			for (CounterThread t : counters) {
				t.stopRunning();
			}
		}
	}

	public static void execute(int nThreads, long counterMax) throws InterruptedException {
		System.out.println("# of threads: " + nThreads);
		System.out.println("Max. counter: " + counterMax);
		System.out.println();

		if (nThreads < 1 || counterMax < 1) {
			System.err.println("Invalid arguments");
			System.exit(1);
		}

		List<CounterThread> threads = new ArrayList<CounterThread>();
		for (int i = 0; i < nThreads; i++) {
			CounterThread t = new CounterThread();
			threads.add(t);
			t.start();
		}

		ReaderThread reader = new ReaderThread(threads, counterMax);
		reader.start();
		reader.join();
	}

	public static void execute(String args[]) throws InterruptedException {
		if (args.length < 2) {
			System.err.println("Too few arguments");
			System.exit(1);
		}

		int nThreads = Integer.parseInt(args[0]);
		long counterMax = Long.parseLong(args[1]);

		StatisticalCounter.execute(nThreads, counterMax);
	}

	public static void main(String args[]) throws InterruptedException {
		if (args.length == 2)
			StatisticalCounter.execute(args);
		else
			StatisticalCounter.execute(NTHREADS, COUNTERMAX);
	}
}
