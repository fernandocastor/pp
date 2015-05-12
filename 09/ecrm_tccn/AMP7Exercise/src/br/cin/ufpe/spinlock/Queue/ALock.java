package br.cin.ufpe.spinlock.Queue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ALock implements Lock {
	
	ThreadLocal<Integer> mySlotIndex = new ThreadLocal<Integer>(){
		protected Integer initialValue() {
			return 0;
		}
	};
	
	AtomicInteger tail;
	volatile boolean[] flag;
	int size;
	
	public ALock(int capacity) {
		size = capacity;
		tail = new AtomicInteger(0);
		flag = new boolean[capacity];
		flag[0] = true;
	}

	@Override
	public void lock() {
		int slot = tail.getAndIncrement() % size;
		mySlotIndex.set(slot);
		while (!flag[slot]) {
		}
	}
	
	@Override
	public void unlock() {
		int slot = mySlotIndex.get();
		flag[slot] = false;
		flag[(slot + 1) % size] = true;
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
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
}
