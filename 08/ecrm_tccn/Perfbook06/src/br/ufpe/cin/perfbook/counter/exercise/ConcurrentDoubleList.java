package br.ufpe.cin.perfbook.counter.exercise;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentDoubleList { //Baseado na seção 6.1.2.2 locktdeq.c

	private Lock lockR; // Lock for list right
	private Lock lockL; // Lock for list left
	// https://docs.oracle.com/javase/tutorial/essential/concurrency/newlocks.html

	private LinkedList listR;
	private LinkedList listL;

	public ConcurrentDoubleList() {

		listR = new LinkedList(); 
		listL = new LinkedList();
		lockL = new ReentrantLock();
		lockR = new ReentrantLock();
	}

	public void push_left(int value) {

		lockL.lock();
		listL.push_left(value);
		lockL.unlock();
	}

	public int pop_right() {

		int value;

		lockR.lock(); // lock right
		value = this.listR.pop_right(); // pop last element
		if (listR.isEmpty()) { // if empty
			this.lockR.unlock(); // unlock right
			this.lockL.lock(); // lock left
			this.lockR.lock(); // lock right
			value = this.listR.pop_right(); // pop right 
			if (listR.isEmpty()) {
				value = this.listL.pop_right();
				this.listR = this.listL;
				this.listL = new LinkedList();
			}
			this.lockL.unlock();
		}
		this.lockR.unlock();

		return value;
	}

	public void push_right(int value) {

		lockR.lock();
		listR.push_right(value);
		lockR.unlock();
	}

	public int pop_left() {

		int value;

		lockL.lock();
		value = this.listL.pop_left();
		if (listL.isEmpty()) {
			lockR.lock();
			if(!listR.isEmpty()){
				value = this.listR.pop_left();
			}

			listL = this.listR;
			listR = new LinkedList();

			lockR.unlock();
		}
		lockL.unlock();
		
		return value;
	}
}