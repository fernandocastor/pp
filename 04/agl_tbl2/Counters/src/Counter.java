import java.util.ArrayList;

public class Counter {

	public static ArrayList<CounterThread> createThreads(String type,
			int numberOfThreads, boolean volatile_) {
		ArrayList<CounterThread> threads = new ArrayList<CounterThread>();
		for (int i = 0; i < numberOfThreads; i++) {
			if (type.equals("int"))
				threads.add(new IntCounterThread(volatile_));
			if (type.equals("long"))
				threads.add(new LongCounterThread(volatile_));
			if (type.equals("float"))
				threads.add(new FloatCounterThread(volatile_));
			if (type.equals("double"))
				threads.add(new DoubleCounterThread(volatile_));
			if (type.equals("atomicinteger"))
				threads.add(new AtomicIntegerCounterThread(volatile_));
			if (type.equals("atomiclong"))
				threads.add(new AtomicLongCounterThread(volatile_));
		}
		return threads;
	}

	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("ERROR, you should run the program as follows:");
			System.out
					.println("Counter <counter_type> <number_of_threads> <counter_limit> [volatile]");
			System.out
					.println("where counter type can be: int, long, float, double, atomicinteger or atomiclong");
			System.exit(1);
		}

		String counterType = args[0];
		if (!counterType.equals("int") && !counterType.equals("long")
				&& !counterType.equals("float")
				&& !counterType.equals("double")
				&& !counterType.equals("atomicinteger")
				&& !counterType.equals("atomiclong")) {
			System.out
					.println("ERROR: Counter type must be: int, long, float, double, atomicinteger or atomiclong");
			System.exit(2);
		}

		int numOfThreads = Integer.parseInt(args[1]);
		long counterLimit = Long.parseLong(args[2]);

		if (numOfThreads <= 0 || counterLimit <= 0) {
			System.out
					.println("ERROR: number of threads and counter limit must be positive integers");
			System.exit(3);
		}

		boolean volatile_ = false;
		if (args.length >= 4)
			volatile_ = args[3].equals("volatile");

		ArrayList<CounterThread> threads = Counter.createThreads(counterType,
				numOfThreads, volatile_);
		ReaderThread reader = new ReaderThread(threads, counterLimit);
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
