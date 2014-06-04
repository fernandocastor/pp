package T07.EX098;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CountDownLatch {

	Condition mayGo;
	ReentrantLock lock;
	volatile int counter;
	
	
	public CountDownLatch(int i) {
		this.counter = i;
		lock = new ReentrantLock();
		mayGo = lock.newCondition();
	}

	public void await() {
		lock.lock();
		try {
			while(counter != 0){
				try {
					mayGo.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} finally{
			lock.unlock();
		}
	}

	public void countDown() {
		lock.lock();
		try{
			counter--;
			mayGo.signalAll();
		}finally{
			lock.unlock();
		}
	}

}
