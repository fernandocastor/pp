

import java.util.concurrent.atomic.AtomicReference;

public class QueueCLHLock extends BaseLock {

	private final AtomicReference<Node> tail;
	private final ThreadLocal<Node> myNodeThreadLocal;
	private final ThreadLocal<Node> myPredecessorThreadLocal;

	public QueueCLHLock() {
		this.tail = new AtomicReference<>(null);
		this.myNodeThreadLocal = new ThreadLocal<Node>() {

			@Override
			protected Node initialValue() {
				return new Node();
			}
		};
		this.myPredecessorThreadLocal = new ThreadLocal<Node>() {
			@Override
			protected Node initialValue() {
				return null;
			}
		};
	}

	@Override
	public void lock() {
		Node myNode = myNodeThreadLocal.get();
		myNode.locked = true;
		final Node pred = tail.getAndSet(myNode);
		if(pred != null) {
			myPredecessorThreadLocal.set(pred);
			while(pred.locked);
		}
	}

	@Override
	public void unlock() {
		final Node myNode = myNodeThreadLocal.get();
		myNode.locked = false;
		final Node pred = myPredecessorThreadLocal.get();
		if(pred != null) myNodeThreadLocal.set(pred);
		else {
			myNodeThreadLocal.set(new Node());
		}
	}

	private static class Node {
		private volatile boolean locked;
	}

	@Override
	public boolean isLocked() {
		final Node currentTail = tail.get();
		return currentTail != null && currentTail.locked;
	}
}
