import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class CountDownLatch {
	int totalThreads;
	Object monitor;

	CountDownLatch(int nthreads) {
		totalThreads = nthreads;
		monitor = new Object();
	}

	public void countDown() {
		synchronized(monitor) {
			totalThreads--;
			if (totalThreads == 0)
				monitor.notifyAll();
		}
	}

	public void await() {
		synchronized(monitor) {
			while (totalThreads > 0) {
				try {
					monitor.wait();
				} catch (Exception e) {}
			}
		}
	}

	public Thread getActive() {
		return new ActiveThread(this);
	}

	public Thread getPassive() {
		return new PassiveThread(this);
	}

	class ActiveThread extends Thread {
		CountDownLatch latch;
		ActiveThread(CountDownLatch latch) {
			this.latch = latch;
		}
	    @Override
	    public void run() {
	    	System.out.println("active thread started!");
	    	// do some work
	    	Random rand = new Random();
	    	int extra = rand.nextInt(5000);
	    	try {
	    		Thread.sleep(1000 + extra);
	    	} catch (Exception e) {}
	    	System.out.println("active thread finished!");
	    	latch.countDown();
	    }
	}

	class PassiveThread extends Thread {
		CountDownLatch latch;
		PassiveThread(CountDownLatch latch) {
			this.latch = latch;
		}
	    @Override
	    public void run() {
	    	latch.await();
	    	System.out.println("passive thread started!");
	    	try {
	    		Thread.sleep(2000);
	    	} catch (Exception e) {}
	    	System.out.println("passive thread finished!");
	    }
	}


	public static void main(String[] args) throws InterruptedException, NoSuchMethodException {
		int nActiveThreads = Integer.parseInt(args[0]);
		CountDownLatch latch = new CountDownLatch(nActiveThreads);
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < nActiveThreads; ++i) {
			Thread t = latch.getActive();
			threads.add(t);
		}
		threads.add(latch.getPassive());
		for (int i = 0; i <= nActiveThreads; ++i) {
			threads.get(i).start();
		}
	}
}