
public class Main {

	static CounterThread[] threads;
	public long k;
	static volatile boolean stop = false;
	
	public Main(long k) {
		this.k = k;
	}
	
	public class CounterThread extends Thread {
		long counter = 0;
		
		void incCounter() {
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
		long counter;
		
		public ReaderThread(CounterThread[] threads) {
			this.threads = threads;
		}
		
		@Override
		public void run() {
			while(!stop) {
				counter = 0;
				for(CounterThread t : threads) {
					if(t != null) {
						counter += t.counter;
					}
				}
//				System.out.println(counter);
				if(counter > k) {
					stop = true;
				}
			}
		}
		
		
	}
	
	public static void main(String[] args) {
		//args[0] = numOfThreads
		//if(args[1] != null): K = args[1], else k = Integer.MAX_VALUE
		if(args.length != 1 && args.length != 2) {
			throw new RuntimeException("Invalid args input.");
		}
		int numOfThreads = Integer.parseInt(args[0]);
		long k;
		if(args.length == 1) {
			k = Integer.MAX_VALUE;
		} else {
			k = Long.parseLong(args[1]);
		}
		Main main = new Main(k);
		threads = new CounterThread[numOfThreads];
		for(int i = 0; i < numOfThreads; i++) {
			threads[i] = main.new CounterThread();
			threads[i].start();
		}
		ReaderThread reader = main.new ReaderThread(threads);
		reader.start();
		
	}
	
}
