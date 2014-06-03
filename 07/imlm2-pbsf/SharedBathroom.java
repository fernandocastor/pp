

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SharedBathroom {

	private static final Random RANDOM = new Random();

	static class Node {
		/*
		 * 0 -> Free
		 * 1 -> Waiting male
		 * 2 -> Acquired male
		 * -1 -> Waiting female
		 * -2 -> Acquired female
		 */
		static final int FREE = 0;
		static final int WAITING_MALE = 1;
		static final int ACQUIRED_MALE = 2;
		static final int WAITING_FEMALE = -1;
		static final int ACQUIRED_FEMALE = -2;
		AtomicInteger state = new AtomicInteger();
	}

	private Lock lock = new ReentrantLock();
	private Condition empty = lock.newCondition();


	private volatile int males;
	private volatile int females;

	private AtomicReference<Node> tail = new AtomicReference<>();
	private ThreadLocal<Node> myNode = new ThreadLocal<Node>() {
		protected Node initialValue() {
			return new Node();
		};
	};

	public void enterMale() {
		lock.lock();
		try{
			enterLocked(Node.WAITING_MALE, Node.ACQUIRED_MALE);
			males++;
		} finally {
			lock.unlock();
		}
	}

	public void leaveMale() {
		lock.lock();
		try {
			males--;
			leaveLocked();
		} finally {
			lock.unlock();
		}
	}

	public void enterFemale() {
		lock.lock();
		try{
			enterLocked(Node.WAITING_FEMALE, Node.ACQUIRED_FEMALE);
			females++;
		} finally {
			lock.unlock();
		}
	}

	public void leaveFemale() {
		lock.lock();
		try {
			females--;
			leaveLocked();
		} finally {
			lock.unlock();
		}
	}

	// Must be called while locked!
	private void enterLocked(final int waitingState, final int acquiredState) {
		final Node node = myNode.get();
		node.state.set(waitingState);
		final Node pred = tail.getAndSet(node);
		if(pred != null) {
			int predState;
			while(true) {
				predState = pred.state.get();
				if(predState == acquiredState || predState == 0) {
					node.state.set(acquiredState);
					break;
				} else {
					try {
						empty.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			node.state.set(acquiredState);
		}
	}

	// Must be called while locked!
	private void leaveLocked() {
		final Node node = myNode.get();
		node.state.set(Node.FREE);
        tail.compareAndSet(node, null);
		if(males == 0 && females == 0) {
			empty.signalAll();
		}
	}

	private class MaleThread extends Thread {
		@Override
		public void run() {
			for (int i = 0; i < 10; i++) {
				enterMale();
				try {
					Thread.sleep(RANDOM.nextInt(100));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				leaveMale();
			}
		}
	}

	private class FemaleThread extends Thread {
		@Override
		public void run() {
			for (int i = 0; i < 10; i++) {
				enterFemale();
				try {
					Thread.sleep(RANDOM.nextInt(100));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				leaveFemale();
			}
		}
	}

	static class Event {
		String gender;
		String type;
		long id;
		public Event(String gender, String type, long id) {
			this.gender = gender;
			this.type = type;
			this.id = id;
		}
		@Override
		public String toString() {
			return "Event [gender=" + gender + ", type=" + type + ", id=" + id
					+ "]";
		}
	}

	public static void main(String[] args) throws InterruptedException {
		SharedBathroom bathroom = new SharedBathroom();
		List<MaleThread> maleThreads = new ArrayList<>();
		List<FemaleThread> femaleThreads = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			maleThreads.add(bathroom.new MaleThread());
			femaleThreads.add(bathroom.new FemaleThread());
		}
		for (int i = 0; i < 10; i++) {
			maleThreads.get(i).start();
			femaleThreads.get(i).start();
		}
		for (FemaleThread femaleThread : femaleThreads) {
			femaleThread.join();
		}
		for (MaleThread maleThread : maleThreads) {
			maleThread.join();
		}
	}

}
