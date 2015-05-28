public class ReadThread extends Thread {

	private ReadLock lock;
	private boolean finish;

	public ReadThread() {
		this.lock = new ReadLock();
	}

	public void finish() {
		finish = true;
	}

	@Override
    public void run() {
		finish = false;
    	while (true) {
    		if (finish)
    			break;
    		lock.lock();
    		try {
    			System.out.println("ReaderLock: " + CounterWriteReader.getValue());
    		} finally {
    			lock.unlock();
    		}
    		try {
    			Thread.sleep(1000);
    		} catch (InterruptedException exception) {
    			exception.printStackTrace();
    		}
    	}
    }
}