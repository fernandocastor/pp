package T07.EX093;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SimpleReadWriteLock {
	int readers;
	boolean writer;
	Lock readLock, writeLock;
	static Object monitor;
	
	
	public SimpleReadWriteLock(){
		monitor = new Object();
		writer = false;
		readers = 0;
		readLock = new ReadLock();
		writeLock = new WriteLock();
	}

	public Lock readLock(){
		return readLock;
	}

	public Lock writeLock(){
		return writeLock;
	}

	class ReadLock implements Lock {
		public void lock(){
			synchronized (monitor) {
				while(writer){
					try {
						System.out.println("reader esperando");
						monitor.wait();							
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				readers++;
			}
		}
		@Override
		public void unlock() {
			synchronized (monitor) {
				System.out.println("reader saiu");
				readers--;
				monitor.notifyAll();
			}
		}
		@Override
		public void lockInterruptibly() throws InterruptedException {
		}

		@Override
		public boolean tryLock() {
			return false;
		}
		@Override
		public boolean tryLock(long time, TimeUnit unit)
				throws InterruptedException {
			return false;
		}
		@Override
		public Condition newCondition() {
			return null;
		} 
	}
	class WriteLock implements Lock {
		public void lock(){
			synchronized (monitor) {
				while(readers > 0 || writer){
					try {
						System.out.println("writer esperando");
						monitor.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				writer = true;
			}
		}

		public void unlock(){
			synchronized (monitor) {
				System.out.println("writer saiu");
				writer = false;
				monitor.notifyAll();
			}
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean tryLock() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit)
				throws InterruptedException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Condition newCondition() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}


