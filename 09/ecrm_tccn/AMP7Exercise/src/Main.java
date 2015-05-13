import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

	public static final int NTHREADS = 10; // 10 50 100 200

	public static void main(String[] args) throws InterruptedException {
		
		question01();
		//question02();
		//question91();
		
	}
	
	private static void question01(){

		List<SpinLockThread> listThreads = new ArrayList<SpinLockThread>();

		long timeStart, timeEnd, result;
		timeStart = System.currentTimeMillis();

		for (int i = 0; i < NTHREADS; i++) {
			SpinLockThread spinLock = new SpinLockThread();
			spinLock.setName("THREAD " + i);
			listThreads.add(spinLock);
		}

		for (SpinLockThread spinLockThread : listThreads) {
			spinLockThread.run();
		}

		timeEnd = System.currentTimeMillis();
		result = timeEnd - timeStart;

//		System.out
//				.println("Finish! Run for " + (result / 1000) + " seconds ");
		System.out.println("Finish! Run for " + (result) + " milliseconds ");
		println(listThreads);
	}
	
	private static void question02(){
		
		List<SpinLockThread> listThreads = new ArrayList<SpinLockThread>();

		long timeStart, timeEnd, result;
		timeStart = System.currentTimeMillis();

		for (int i = 0; i < NTHREADS; i++) {
			SpinLockThread spinLock = new SpinLockThread(NTHREADS);
			spinLock.setName("THREAD " + i);
			listThreads.add(spinLock);
		}

		for (SpinLockThread spinLockThread : listThreads) {
			spinLockThread.run();
		}

		timeEnd = System.currentTimeMillis();
		result = timeEnd - timeStart;

//		System.out
//				.println("Finish! Run for " + (result / 1000) + " seconds ");
		System.out.println("Finish! Run for " + (result) + " milliseconds ");
		println(listThreads);	
	}
	
	private static void question91(){
		
		List<SpinLockThread> listThreads = new ArrayList<SpinLockThread>();

		long timeStart, timeEnd, result;
		timeStart = System.currentTimeMillis();

		for (int i = 0; i < NTHREADS; i++) {
			SpinLockThread spinLock = new SpinLockThread();
			spinLock.setName("THREAD " + i);
			listThreads.add(spinLock);
		}

		for (SpinLockThread spinLockThread : listThreads) {
			spinLockThread.run();
		}

		timeEnd = System.currentTimeMillis();
		result = timeEnd - timeStart;

//		System.out
//				.println("Finish! Run for " + (result / 1000) + " seconds ");
		System.out.println("Finish! Run for " + (result) + " milliseconds ");
	}

	private static void println(List<SpinLockThread> threads) {
		long number = 0, avg = 0;;
		for (SpinLockThread spinLockThread : threads) {
			System.out.println(spinLockThread.getName() + " - "
					+ spinLockThread.counter.number);
			number += spinLockThread.counter.number;
		}
		avg = number/NTHREADS;
		System.out.println("Average:   " + avg);
	}
}