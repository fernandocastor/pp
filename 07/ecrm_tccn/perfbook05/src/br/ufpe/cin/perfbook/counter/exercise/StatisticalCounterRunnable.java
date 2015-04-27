package br.ufpe.cin.perfbook.counter.exercise;

public class StatisticalCounterRunnable implements Runnable {

	 // id to identify the counter
    private int counterId;
    
    // local counter
    private long counter = 0;
    
    // listener to be called when execution is finished
    private StatisticalCounterListener listener;
    
    // flag to identify if the limit was reached
    private volatile boolean limitReached = false;

    public StatisticalCounterRunnable(int counterId, StatisticalCounterListener listener) {
        this.counterId = counterId;
        this.listener = listener;
    }
    
    @Override
    public void run() {
        // increment the counter while the limit is not reached
        while (!this.limitReached) {
            this.counter++;
        }
        
        // tell someone that the execution was finished
        if (this.listener != null) {
            this.listener.onExecutionFinished(this.counterId);
        }
    }
    
    public long getCounterValue() {
        return this.counter;
    }

    public void setLimitReached(boolean limitReached) {
        this.limitReached = limitReached;
    }

}
