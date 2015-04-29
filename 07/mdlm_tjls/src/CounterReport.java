public class CounterReport {
	public static void main(String[] args) throws InterruptedException {
		long [] executionTimes = new long[3];

		for (int i = 0; i < 10; i++) {
			System.out.println("EXECUTION # " + (i+1));
			long startTime = System.currentTimeMillis();
			ExactCounter.call(8, (1L << 32));
			long endTime = System.currentTimeMillis();
			
			if (i >= 10 - executionTimes.length) {
				executionTimes[i - (10 - executionTimes.length)] = endTime - startTime;
			}
		}

		long sumTimes = 0;
		for (int i = 0; i < executionTimes.length; i++) {
			sumTimes += executionTimes[i];
			System.out.println("Execution time " + i + ": " + executionTimes[i]);
		}

		System.out.println("Average: " + (sumTimes / executionTimes.length));
	}
}
