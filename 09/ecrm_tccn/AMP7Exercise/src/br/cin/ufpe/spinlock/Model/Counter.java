package br.cin.ufpe.spinlock.Model;

import br.cin.ufpe.spinlock.Queue.ALock;
import br.cin.ufpe.spinlock.TTAS.TTASLock;

public class Counter {

	public volatile int number;

	//private TASLock mutex;
	//private TTASLock mutex;
	//private ReentrantLock mutex;
	
	private ALock mutex;

	public Counter() {
		//mutex = new TASLock();
		//mutex = new TTASLock();
		//mutex = new ReentrantLock();
		this.number = 0;
	}
	
	public Counter(int capacity) {
		this();
		mutex = new ALock(capacity);
	}

	public void incrementar() {
		mutex.lock();
		try {
			number++;
		} catch (Exception ex) {

		} finally {
			mutex.unlock();
		}
	}
}