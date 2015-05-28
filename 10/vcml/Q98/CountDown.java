package Q98;

import java.util.concurrent.CountDownLatch;

public class CountDown {

	public static void main(String[] args) {
		int numberOfActiveThreads = 5;
		int numberOfPassiveThreads = 5;
		CountDownLatch signal = new CountDownLatch(numberOfActiveThreads);
		
		for (int i = 0; i < numberOfActiveThreads; ++i) {
			new Thread(new ActiveThread((i+1),signal)).start();
		}

		for (int i = 0; i < numberOfPassiveThreads; ++i) {
			new Thread(new PassiveThread((i+1),signal)).start();
		}
	}

}
