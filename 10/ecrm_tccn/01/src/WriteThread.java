public class WriteThread extends Thread {

	private WriteLock lock;
	private boolean finish;

	public WriteThread() {
		this.lock = new WriteLock();
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
				CounterWriteReader.increment();
				System.out.println("WriterLock increment value");
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