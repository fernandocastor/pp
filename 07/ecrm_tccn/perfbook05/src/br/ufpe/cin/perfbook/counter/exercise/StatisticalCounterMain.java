package br.ufpe.cin.perfbook.counter.exercise;

// Statistical Counters é atualizada muito frequentemente, e o valor é lido nunca ou raramente.
public class StatisticalCounterMain {

	  public static void main(String[] args) {
		  
		  StatisticalCounter counter = new StatisticalCounter();
		  
		  // create the thread that will be responsible to read the counter
		  Thread readerThread = new Thread(new StatisticalReaderRunnable(counter));
		  readerThread.start();
		  
		  counter.start();
	    }	
}