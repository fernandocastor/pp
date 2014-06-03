

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SyncSharedBathroom {

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

	private Object monitor = new Object();

	private volatile int males;
	private volatile int females;

	private AtomicReference<Node> tail = new AtomicReference<>();
	private ThreadLocal<Node> myNode = new ThreadLocal<Node>() {
		protected Node initialValue() {
			return new Node();
		};
	};

	public void enterMale() {
		synchronized(monitor){
			enterLocked(Node.WAITING_MALE, Node.ACQUIRED_MALE);
			males++;
		}
	}

	public void leaveMale() {
		synchronized(monitor) {
			males--;
			leaveLocked();
		}
	}

	public void enterFemale() {
		synchronized(monitor) {
			enterLocked(Node.WAITING_FEMALE, Node.ACQUIRED_FEMALE);
			females++;
		}
	}

	public void leaveFemale() {
		synchronized(monitor) {
			females--;
			leaveLocked();
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
						monitor.wait();
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
		if(!tail.compareAndSet(node, null)) {
			// We can not reuse the predecessor because it may not have finished
			myNode.set(new Node());
		}
		if(males == 0 && females == 0) {
			monitor.notifyAll();
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

	public static void main(String[] args) throws InterruptedException {
		SyncSharedBathroom bathroom = new SyncSharedBathroom();
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
