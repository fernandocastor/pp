import java.util.concurrent.locks.Lock;

public class WriterThread extends Thread {
	Counter counter;
	Lock lock;
	boolean running;

	public WriterThread(Counter counter, Lock writerLock) {
		this.counter = counter;
		this.lock = writerLock;
	}

	public boolean isRunning() {
		return running;
	}

	public void finish() {
		running = false;
	}

	@Override
    public void run() {
    	running = true;
    	while (true) {
    		if (!isRunning())
    			break;
    		lock.lock();
    		try {
    			counter.increment();
    			System.out.println("Writer " + Thread.currentThread() + " incremented counter value: " + counter.getCount());
    		} finally {
    			lock.unlock();
    		}
    		try {
    			Thread.sleep(10000);
    		} catch (Exception e) {}
    	}
    }
}