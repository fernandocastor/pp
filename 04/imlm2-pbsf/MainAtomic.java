import java.util.concurrent.atomic.AtomicLong;

public class MainAtomic {

	static CounterThread[] threads;
	public long k;
	static volatile boolean stop = false;
	
	public MainAtomic(long k) {
		this.k = k;
	}
	
	public class CounterThread extends Thread {
		AtomicLong counter = new AtomicLong();
		
		void incCounter() {
			counter.getAndIncrement();
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
		AtomicLong counter;
		
		public ReaderThread(CounterThread[] threads) {
			this.threads = threads;
		}
		
		@Override
		public void run() {
			while(!stop) {
				counter = new AtomicLong();
				for(CounterThread t : threads) {
					if(t != null) {
						counter.getAndAdd(t.counter.get());
					}
				}
				if(counter.get() >= k) {
					stop = true;
				}
			}
		}
		
		
	}
	
	public static void main(String[] args) {
		//args[0] = numOfThreads
		//args[1] = K
		if(args.length != 2 && args.length != 1) {
			throw new RuntimeException("Invalid args input.");
		}
		int numOfThreads = Integer.parseInt(args[0]);
		long k;
		if(args.length == 1) {
			k = Integer.MAX_VALUE;
		} else {
			k = Long.parseLong(args[1]);
		}
		MainAtomic main = new MainAtomic(k);
		threads = new CounterThread[numOfThreads];
		for(int i = 0; i < numOfThreads; i++) {
			threads[i] = main.new CounterThread();
			threads[i].start();
		}
		ReaderThread reader = main.new ReaderThread(threads);
		reader.start();
		
	}
	
}
