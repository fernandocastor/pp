package pp.T06ex02;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CLHLock implements Lock {
	class QNode {
		boolean locked = false;
		QNode next = null;
	}

	AtomicReference<QNode> tail = new AtomicReference<QNode>(new QNode());
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

	@Override
	public void lock() {
		QNode qnode = myNode.get();
		qnode.locked = true;
		QNode pred = tail.getAndSet(qnode);
		myPred.set(pred);
		while (pred.locked) {
			// System.out.println("estou preso:" +
			// Thread.currentThread().getId());
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
		qnode.locked = false;
		myNode.set(myPred.get());
	}
}
