public abstract class CounterThread extends Thread {

	private boolean mRun = true;
	protected boolean mVolatile;

	public CounterThread(boolean volatile_) {
		mVolatile = volatile_;
	}

	public abstract void increment();

	public abstract long getSum();

	public void stopCounter() {
		mRun = false;
	}

	@Override
	public void run() {
		while (mRun) {
			// try {
			// sleep(1);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			increment();
		}
	}
}
