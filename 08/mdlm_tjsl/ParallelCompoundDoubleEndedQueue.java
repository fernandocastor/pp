import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ParallelCompoundDoubleEndedQueue<T> {
	private LinkedList<T> ldeq, rdeq;
	private Lock leftLock, rightLock;

	public ParallelCompoundDoubleEndedQueue() {
		this.ldeq = new LinkedList<T>();
		this.rdeq = new LinkedList<T>();

		this.leftLock = new ReentrantLock();
		this.rightLock = new ReentrantLock();
	}

	public T popLeft() {
		T e;

		this.leftLock.lock();
		e = this.ldeq.removeFirst();
		if (e == null) {
			this.rightLock.lock();
			e = this.rdeq.removeFirst();

			// XXX
			this.ldeq = this.rdeq;
			this.rdeq = new LinkedList<T>();

			this.rightLock.unlock();
		}
		this.leftLock.unlock();

		return e;
	}

	public T popRight() {
		T e;

		this.rightLock.lock();
		e = this.rdeq.removeLast();
		if (e == null) {
			this.rightLock.unlock();
			this.leftLock.lock();
			this.rightLock.lock();
			e = this.rdeq.removeLast();
			if (e == null) {
				e = this.ldeq.removeLast();

				// XXX
				this.rdeq = this.ldeq;
				this.ldeq = new LinkedList<T>();
			}
			this.leftLock.unlock();
		}
		this.rightLock.unlock();

		return e;
	}

	public void pushLeft(T e) {
		this.leftLock.lock();
		this.ldeq.addFirst(e);
		this.leftLock.unlock();
	}

	public void pushRight(T e) {
		this.rightLock.lock();
		this.rdeq.addLast(e);
		this.rightLock.unlock();
	}

	public static void main(String args[]) {
		System.out.println("teste");
	}
}
