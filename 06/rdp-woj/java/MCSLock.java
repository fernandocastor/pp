package pp.T06ex02;

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
