import java.util.ArrayList;

public class Counter {

	public static ArrayList<CounterThread> createThreads(int numberOfThreads,
			ReaderThread reader) {
		ArrayList<CounterThread> threads = new ArrayList<CounterThread>();
		for (int i = 0; i < numberOfThreads; i++) {
			threads.add(new CounterThread(reader));
		}
		return threads;
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("ERROR, you should run the program as follows:");
			System.out
					.println("ExactCounter <number_of_threads> <counter_limit>");
			System.exit(1);
		}

		int numOfThreads = Integer.parseInt(args[0]);
		long counterLimit = Long.parseLong(args[1]);

		if (numOfThreads <= 0 || counterLimit <= 0) {
			System.out
					.println("ERROR: number of threads and counter limit must be positive integers");
			System.exit(3);
		}

		ReaderThread reader = new ReaderThread(counterLimit);
		ArrayList<CounterThread> threads = Counter.createThreads(numOfThreads,
				reader);
		reader.setThreads(threads);
		for (CounterThread counter : threads)
			counter.start();

		reader.start();

		for (CounterThread counter : threads) {
			try {
				counter.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			reader.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
