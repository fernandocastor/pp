import java.util.concurrent.atomic.AtomicReference;


public class QueueMCSLock extends BaseLock {

	static class Node {
		volatile boolean locked;
		volatile Node next;
	}

	private final AtomicReference<Node> tail;
	private final ThreadLocal<Node> myNodeThreadLocal;


	protected QueueMCSLock() {
		this.tail = new AtomicReference<>();
		this.myNodeThreadLocal = new ThreadLocal<Node>() {
			@Override
			protected Node initialValue() {
				return new Node();
			}
		};
	}


	@Override
	public void lock() {
		final Node node = myNodeThreadLocal.get();
		node.locked = true;
		final Node prevNode = tail.getAndSet(node);
		if(prevNode != null) {
			prevNode.next = node;
			while(node.locked);
		}
	}

	@Override
	public void unlock() {
		final Node node = myNodeThreadLocal.get();
		node.locked = false;
		if(node.next == null) {
			if(tail.compareAndSet(node, null))
				return;
			while(node.next == null);
		}
		node.next.locked = false;
		node.next = null;
	}

	@Override
	public boolean isLocked() {
		final Node currentTail = tail.get();
		return currentTail != null && currentTail.locked;
	}

}
