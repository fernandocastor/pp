import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ParallelHashedDoubleEndedQueue<T> {
	private static final int DEQ_N_BKTS = 4;
	private LinkedList<T> [] deq;
	private Lock [] bucketLock;
	private Lock leftLock, rightLock;
	private int lidx, ridx;

	public ParallelHashedDoubleEndedQueue() {
		this.deq = (LinkedList<T>[]) new LinkedList[DEQ_N_BKTS];
		this.bucketLock = new Lock[DEQ_N_BKTS];
		for (int i = 0; i < DEQ_N_BKTS; i++) {
			this.deq[i] = new LinkedList<T>();
			this.bucketLock[i] = new ReentrantLock();
		}

		this.leftLock = new ReentrantLock();
		this.rightLock = new ReentrantLock();
	}

	public T popLeft() {
		T e;
		int i;

		this.leftLock.lock();
		i = moveRight(this.lidx);
		this.bucketLock[i].lock();
		e = this.deq[i].pollFirst();
		if (e != null)
			this.lidx = i;
		this.bucketLock[i].unlock();
		this.leftLock.unlock();

		return e;
	}

	public T popRight() {
		T e;
		int i;

		this.rightLock.lock();
		i = moveLeft(this.ridx);
		this.bucketLock[i].lock();
		e = this.deq[i].pollLast();
		if (e != null)
			this.ridx = i;
		this.bucketLock[i].unlock();
		this.rightLock.unlock();

		return e;
	}

	public void pushLeft(T e) {
		this.leftLock.lock();
		int i = this.lidx;
		this.bucketLock[i].lock();
		this.deq[i].addFirst(e);
		this.lidx = moveLeft(this.lidx);
		this.bucketLock[i].unlock();
		this.leftLock.unlock();
	}

	public void pushRight(T e) {
		this.rightLock.lock();
		int i = this.ridx;
		this.bucketLock[i].lock();
		this.deq[i].addLast(e);
		this.ridx = moveRight(this.ridx);
		this.bucketLock[i].unlock();
		this.leftLock.unlock();
	}

	private static int moveLeft(int i) {
		return i == 0 ? DEQ_N_BKTS-1 : i-1;
	}

	private static int moveRight(int i) {
		return (i+1) % DEQ_N_BKTS;
	}
}
