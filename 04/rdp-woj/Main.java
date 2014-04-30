package T04;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Main {

	static int numThreads = 8;
	static long numLimite = 2147483000;// Integer.MAX_VALUE -> 2147483647
	static Boolean stopFlag = true;
	static ArrayList<ThreadCount> listaThread = new ArrayList<ThreadCount>();
	static int soma = 0;

	final static CountDownLatch firstGate = new CountDownLatch(1);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long begin = System.nanoTime();
		// Criando as threads
		for (int i = 0; i < numThreads; i++) {
			listaThread.add(new ThreadCount(stopFlag, firstGate));
		}

		startThreads();

		while (soma <= numLimite) {
			soma = 0;
			if (numThreads == listaThread.size()) {
				for (ThreadCount thread : listaThread) {
					soma += thread.getCont();
				}
				try {
					Thread.currentThread().sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println(soma);
		}

		stopAllThreads();
		System.out.println("Fim!");
		long end = System.nanoTime();
		System.out.println("time:" + (end - begin));
		System.exit(0);
	}

	/**
	 * ativa o run das threads
	 */
	public static void startThreads() {
		for (ThreadCount thread : listaThread) {
			thread.start();
		}
		// start em todas as threads ao mesmo tempo
		firstGate.countDown();
	}

	/**
	 * Param todas as threads
	 */
	public static void stopAllThreads() {
		stopFlag = false;
		for (ThreadCount thread : listaThread) {
			thread.setStopFlag(stopFlag);
		}
	}
}
