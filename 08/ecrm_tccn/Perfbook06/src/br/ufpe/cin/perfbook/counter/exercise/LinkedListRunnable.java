package br.ufpe.cin.perfbook.counter.exercise;

public class LinkedListRunnable implements Runnable {

	private final int limit = 1000000; //Change 10000 OR 1000000
	private int element = 0;
	
	@Override
	public void run() {
		
		//Sequential
		//LinkedList list = new LinkedList();
		
		//Concurrent
		ConcurrentDoubleList list = new ConcurrentDoubleList();
		
		// Enqueue Operation
		do {

			list.push_left(element);
			element++;
			
			
		} while (element < limit);
		
		// Dequeue Operation
		do {
			//printLock(list);
			list.pop_left();
			element--;
			
			
		} while (element > 0);
	}
	
	private synchronized void printLock(ConcurrentDoubleList list){
		
		System.out.println(list.pop_left());
	}
}
