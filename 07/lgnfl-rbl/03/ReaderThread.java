import java.util.concurrent.locks.Lock;

public class ReaderThread extends Thread {
	Counter counter;
	Lock lock;
	boolean running;

	public ReaderThread(Counter counter, Lock readerLock) {
		this.counter = counter;
		this.lock = readerLock;
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
    			System.out.println("Reader " + Thread.currentThread() + " check counter value: " + counter.getCount());
    		} finally {
    			lock.unlock();
    		}
    		try {
    			Thread.sleep(1000);
    		} catch (Exception e) {}
    	}
    }
}