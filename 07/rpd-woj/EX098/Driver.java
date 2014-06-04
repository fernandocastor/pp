package T07.EX098;

public class Driver {

	static int n = 4;
	static String args = "fazendo algo ";
	
	public static void main(String[] args) {
		CountDownLatch startSignal = new CountDownLatch(1);
		CountDownLatch doneSignal = new CountDownLatch(n);
		
		for (int i = 0; i < n; i++) {
			new Thread(new Worker(startSignal,doneSignal)).start();
		}
		
		doSomethingElse();
		startSignal.countDown();
		doSomethingElse();
		doneSignal.await();
	}

	private static void doSomethingElse() {
		System.out.println(args);	
	}
}
class Worker implements Runnable {
	private final CountDownLatch startSignal, doneSignal;
	String args = "Trabalhando ";
	
	Worker( CountDownLatch myStartSignal, CountDownLatch myDoneSignal) {
		this.startSignal = myStartSignal;
		this.doneSignal = myDoneSignal;
	}

	@Override
	public void run() {
		startSignal.await();
		doWork();
		doneSignal.countDown();
	}

	private void doWork() {
		System.out.println(args);
		
	}
	
}
