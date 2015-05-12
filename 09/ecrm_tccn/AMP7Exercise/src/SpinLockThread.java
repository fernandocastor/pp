import br.cin.ufpe.spinlock.Model.Counter;

public class SpinLockThread extends Thread {

	public Counter counter;

	public SpinLockThread() {
		this.counter = new Counter();
	}
	
	public SpinLockThread(int capacity) {
		this.counter = new Counter(capacity);
	}

	@Override
	public void run() {
		
		//perNThreads();
		oneThousandExecutions();
		
	}	
	private void perNThreads(){
		
		long timeStart, timeEnd, result;
		timeStart = System.currentTimeMillis();
		
		 do {
		 counter.incrementar();
		 timeEnd = System.currentTimeMillis();
		 result = timeEnd - timeStart;
		 } while (result < (120000 / Main.NTHREADS));
	}
	
	private void oneThousandExecutions(){
		for (int i = 0; i < 1000; i++) {
			counter.incrementar();
		}
	}
}
