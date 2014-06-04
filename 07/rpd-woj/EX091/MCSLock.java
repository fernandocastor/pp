package exercicio_91_MCS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MCSLock implements Lock {

	class QNode {
		boolean locked = false;
		QNode next = null;
	}

	AtomicReference<QNode> tail;
	ThreadLocal<QNode> myNode;

	public MCSLock() {
		tail = new AtomicReference<QNode>(null);
		myNode = new ThreadLocal<QNode>() {
			@Override
			protected QNode initialValue() {
				return new QNode();
			}
		};
	}

	private boolean isLocked() {
		return tail == null;
	}
	
	@Override
	public void lock() {
		QNode qnode = myNode.get();
		QNode pred = tail.getAndSet(qnode);
		if (pred != null) {
			qnode.locked = true;
			pred.next = qnode;
			// wait until predecessor gives up the lock
			while (qnode.locked) {
			}
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
	}

	@Override
	public Condition newCondition() {
		return null;
	}

	@Override
	public boolean tryLock() {
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit)
			throws InterruptedException {
		return false;
	}

	@Override
	public void unlock() {
		QNode qnode = myNode.get();
		if (qnode.next == null) {
			if (tail.compareAndSet(qnode, null)) {
				return;
			}
			// wait until predecessor fills in its next field
			while (qnode.next == null) {
			}
		}
			qnode.next.locked = false;
			qnode.next = null;
	}
}
