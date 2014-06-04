package exercicio_91_CLH;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CLHLock implements Lock {
	class QNode {
		boolean locked = false;
		QNode next = null;
	}

	AtomicReference<QNode> tail;
	ThreadLocal<QNode> myPred;
	ThreadLocal<QNode> myNode;

	public CLHLock() {
		tail = new AtomicReference<QNode>(new QNode());
		myNode = new ThreadLocal<QNode>() {
			@Override
			protected QNode initialValue() {
				return new QNode();
			}
		};
		myPred = new ThreadLocal<QNode>() {
			@Override
			protected QNode initialValue() {
				return null;
			}
		};
	}

	private boolean isLocked() {
		return tail.get().locked;
	}
	
	@Override
	public void lock() {
		QNode qnode = myNode.get();
		qnode.locked = true;
		QNode pred = tail.getAndSet(qnode);
		myPred.set(pred);
		while (pred.locked) {
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

		qnode.locked = false;
		myNode.set(myPred.get());
	}
}
