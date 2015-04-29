package br.ufpe.cin.perfbook.counter.exercise;

public class StatisticalReaderRunnable implements Runnable  {
	
	private StatisticalCounter counter;
	
	 public StatisticalReaderRunnable(StatisticalCounter counter) {
	        this.counter = counter;
	    }

	    @Override
	    public void run() {
	        long current = 0;

	        do {
	            current = this.counter.read();
	            System.out.println("Current counter value is: " + current);
	        } while (!this.counter.isLimitReached());
	    }
}
