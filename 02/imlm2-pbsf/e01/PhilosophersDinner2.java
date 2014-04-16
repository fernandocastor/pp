package sandbox;

import java.util.ArrayList;
import java.util.List;

public class PhilosophersDinner2 {

	static class Philosopher extends Thread {
		Chopstick leftChopstick;
		Chopstick rightChopstick;

		Philosopher(Chopstick left, Chopstick right) {
			this.leftChopstick = left;
			this.rightChopstick = right;
		}

		void eat() throws InterruptedException {
			boolean lTaken = false;
			boolean rTaken = false;
			while(!(lTaken = leftChopstick.tryTake()) || !(rTaken = rightChopstick.tryTake())) {
				if(lTaken) leftChopstick.putDown();
				if(rTaken) rightChopstick.putDown();
			}
			Thread.sleep(100); // Let them digest!
		}

		void think() throws InterruptedException {
			leftChopstick.putDown();
			rightChopstick.putDown();
			Thread.sleep(100); // Let reason!
		}

		void eatAndThinkForever() throws InterruptedException {
			//while(true) {
				eat();
				think();
			//}
		}

		@Override
		public void run() {
			try {
				eatAndThinkForever();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static class Chopstick {
		boolean taken = false;
		int id;


		protected Chopstick(int id) {
			this.id = id;
		}

		synchronized boolean tryTake() {
			boolean isTaken = false;
			if(!taken) {
				isTaken = taken = true;
				System.out.println(Thread.currentThread().getId() + " got me (" + id + ")");
			}
			return isTaken;
		}

		synchronized void putDown(){
			taken = false;
			System.out.println(Thread.currentThread().getId() + " released me (" + id + ")");
		}
	}

	public static final int PHILOSOPHERS_NUMBER = 5;

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			doRun();
		}
	}

	private static void doRun() throws InterruptedException {
		final int chopsticksNumber = PHILOSOPHERS_NUMBER;
		List<Chopstick> chopsticks = new ArrayList<>(chopsticksNumber);
		for (int i = 0; i < chopsticksNumber; i++) {
			chopsticks.add(new Chopstick(i));
		}
		List<Philosopher> philosophers = new ArrayList<>(PHILOSOPHERS_NUMBER);
		for (int i = 0; i < PHILOSOPHERS_NUMBER; i++) {
			Chopstick left = chopsticks.get(i % chopsticksNumber);
			Chopstick right = chopsticks.get((i + 1) % chopsticksNumber);
			philosophers.add(new Philosopher(left, right));
		}
		for (Philosopher philosopher : philosophers) {
			philosopher.start();
		}
		for (Philosopher philosopher : philosophers) {
			philosopher.join();
		}
	}


}
