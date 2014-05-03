import java.util.ArrayList;
import java.util.List;

/**
 * Tentative implementation
 *
 */
public class MainExact {

	private final long K;
	private volatile boolean stop = false;
	
	public MainExact(long k) {
		this.K = k;
	}
	
	public class CounterThread extends Thread {
		public volatile long counter = 0;
		private final long countermax;

		
		protected CounterThread(long countermax) {
			this.countermax = countermax;
		}

		void incCounter() {
			if(counter < countermax)
				counter++;
		}
		
		@Override
		public void run() {
			while(!stop) {
				incCounter();
			}
		}
	}
	
	public class ReaderThread extends Thread {
		CounterThread[] threads;
		
		public ReaderThread(CounterThread[] threads) {
			this.threads = threads;
		}
		
		@Override
		public void run() {
			while(!stop) {
				long counter = 0;
				for(CounterThread t : threads) {
					if(t != null) {
						counter += t.counter;
					}
				}
				if(counter >= K) {
					stop = true;
				}
			}
		}
		
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		if(args.length < 2) {
			throw new RuntimeException("Invalid args input.");
		}
		int numOfThreads = Integer.parseInt(args[0]);
		long k;
		if(args.length == 1) {
			k = Integer.MAX_VALUE;
		} else {
			k = Long.parseLong(args[1]);
		}

		MainExact main = new MainExact(k);
		final List<Long> countermaxes = partition(k, numOfThreads);
		CounterThread[] threads = new CounterThread[numOfThreads];
		for(int i = 0; i < numOfThreads; i++) {
			long countermax = countermaxes.get(i);
			threads[i] = main.new CounterThread(countermax);
			threads[i].start();
		}
		ReaderThread reader = main.new ReaderThread(threads);
		reader.start();
		reader.join();
		
		long sum = 0;
		for (CounterThread counterThread : threads) {
			counterThread.join();
			sum += counterThread.counter;
		}
		System.out.println(sum);
	}

	static List<Long> partition(long countermax, long threads) {
		List<Long> countermaxes = new ArrayList<Long>();
		long base = countermax/threads;
		long basePlusOneCount = countermax % threads;
		for (int i = 0; i < basePlusOneCount; i++) {
			countermaxes.add(base + 1);
		}
		for (int i = 0; i < threads - basePlusOneCount; i++) {
			
			countermaxes.add(base);
		}
		return countermaxes;
	}
}
