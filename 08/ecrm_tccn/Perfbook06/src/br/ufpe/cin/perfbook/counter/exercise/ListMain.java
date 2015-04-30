package br.ufpe.cin.perfbook.counter.exercise;

public class ListMain {
	
	public static void main(String[] args) {
		
		//Sequencial
		//sequentialImplementation();
			
		//Concurrent
		concurrentImplementation();
	}
	
	private static void concurrentImplementation(){
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			Thread thread = new Thread(new LinkedListRunnable());
			thread.run();	
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Times: " + (endTime - startTime) + " milliseconds " + "or " + (endTime - startTime) / 1000 + " seconds ");	
	}
	
	private static void sequentialImplementation(){
		
		long startTime = System.currentTimeMillis();
		Thread onethread = new Thread(new LinkedListRunnable());
		onethread.run();	
		long endTime = System.currentTimeMillis();
		System.out.println("Times: " + (endTime - startTime) + " milliseconds " + "or " + (endTime - startTime) / 1000 + " seconds ");
	}
}