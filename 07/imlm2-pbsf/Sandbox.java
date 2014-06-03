

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;


public class Sandbox {

	static volatile long counter = 0;
	public static void main(String[] args) throws InterruptedException {
		final EventMonitoring monitor = new EventMonitoring(20000000);
		Lock lock = new QueueMCSLock(monitor);

		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < 4; i++) {
			final Thread thread = new Thread(() -> {
				while(counter < 500000L) {
					lock.lock();
					try {
						counter++;
					} finally {
						lock.unlock();
					}
				}

			});
			threads.add(thread);
			thread.start();
		}

		/*long timeout = 2 * 60 * 1000;
		long start;
		for (int j = 0; j < 4; j++) {
			start = System.currentTimeMillis();
			while(System.currentTimeMillis() - start <  timeout) Thread.sleep(timeout/10);
			List<EventMonitoring.Event> events = monitor.getEvents();
			StringBuffer buffer = new StringBuffer();
			for (int i = Math.max(0, events.size() - 50); i < events.size(); i++) {
				buffer.append(i + " - " + events.get(i) + "\n");
			}
			System.out.println(buffer);
			System.out.println("\n#########################################\n");
		}
		System.exit(0);*/
	}

}
