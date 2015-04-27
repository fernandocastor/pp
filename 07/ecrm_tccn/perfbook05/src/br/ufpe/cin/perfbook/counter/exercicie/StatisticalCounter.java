package br.ufpe.cin.perfbook.counter.exercicie;

public class StatisticalCounter {
	
	   // number of threads that will increment the counter
    private static final int N = 100;

    // the limit to be reached by counter
    private static final long K = (long) Math.pow(2, 33);

    // flag to identify if the limit was reached on the sum of thread counters
    private volatile boolean limitReached = false;

    // the counters that will provide the total value of the current counter
    private StatisticalCounterRunnable[] counters;
    
    // variables to identify execution time
    private long startTime;
    private int unregisteredCounters = 0;

    public StatisticalCounter() {
        // just create the array of counters
        this.counters = new StatisticalCounterRunnable[N];
    }
    
    public void start() {
        this.startTime = System.currentTimeMillis();
        
        // create the N counter threads and register it on counters list
        for (int i = 0; i < this.counters.length; i++) {
            this.counters[i] = new StatisticalCounterRunnable(i, listener);
            Thread counterThread = new Thread(this.counters[i]);
            counterThread.start();
        }
    }

    public synchronized long read() {
        long current = 0;

        // iterate over counters in order to retrieve the total
        for (int i = 0; i < this.counters.length; i++) {
            if (this.counters[i] != null) {
                current += this.counters[i].getCounterValue();
            }
        }

        // verify if the limit was reached
        if (current >= K || current < 0) {
            this.tellEveryoneTheLimitWasReached();
        }

        return current;
    }

    public boolean isLimitReached() {
        return this.limitReached;
    }

    private void tellEveryoneTheLimitWasReached() {
        for (int i = 0; i < this.counters.length; i++) {
            if (this.counters[i] != null) {
                this.counters[i].setLimitReached(true);
            }
        }

        this.limitReached = true;
    }
    
    private synchronized void unregisterCounter(int id, long current) {
        unregisteredCounters++;
        if (unregisteredCounters >= N) {
        	  float spentTime = (float)(System.currentTimeMillis() - startTime) / 1000f;
              System.out.println("Time spent: " + spentTime + " seconds");
              
              if (current < 0) {
                  System.out.println("Dude, it happened an overflow!");
              }
          }
      }

      // it is just a listener to identify when the thread was finished
      private StatisticalCounterListener listener = new StatisticalCounterListener() {

          @Override
          public void onExecutionFinished(int counterId) {
              long current = read();
              System.out.println("Counter " + counterId + " finished and value read was " + current);
              unregisterCounter(counterId, current);
          }
      };
  }