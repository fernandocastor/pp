import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedALQueue<T> {

	private final T[] nodes;
	private final Lock enqLock;
	private final Condition queueNotEmpty;
	private final Lock deqLock;
	private final Condition queueNotFull;

	// Talvez seja necessario usar AtomicInt
	private volatile int head;
	private volatile int tail;

	@SuppressWarnings("unchecked")
	public BoundedALQueue(int capacity) {
		this.nodes = (T[]) new Object[capacity];
		this.enqLock = new ReentrantLock();
		this.queueNotFull = enqLock.newCondition();
		this.deqLock = new ReentrantLock();
		this.queueNotEmpty = deqLock.newCondition();
	}

	private int size() {
		return tail - head;
	}

	public void enq(T t) throws InterruptedException {
		boolean notifyNotEmpty = false;
		enqLock.lock();
		try {
			while(size() == nodes.length) queueNotFull.await();
			nodes[tail % nodes.length] = t;
			if((tail++ - head) == 0) {
				notifyNotEmpty = true;
			}
		} finally {
			enqLock.unlock();
		}

		if(notifyNotEmpty) {
			deqLock.lock();
			try {
				queueNotEmpty.signal();
			} finally {
				deqLock.unlock();
			}
		}
	}

	public T deq() throws InterruptedException{
		T val;
		boolean notifyNotFull = false;
		deqLock.lock();
		try {
			while(size() == 0) queueNotEmpty.await();
			val = nodes[head % nodes.length];
			if((tail - head++) == nodes.length) {
				notifyNotFull = true;
			}
		} finally  {
			deqLock.unlock();
		}

		if(notifyNotFull) {
			enqLock.lock();
			try {
				queueNotFull.signal();
			} finally {
				enqLock.unlock();
			}
		}
		return val;
	}
}



import java.util.concurrent.atomic.AtomicMarkableReference;

public class BoundALockfreeQueue<T> {

	private final T[] nodes;
	private final AtomicMarkableReference<Integer> head;
	private final AtomicMarkableReference<Integer> tail;

	@SuppressWarnings("unchecked")
	public BoundALockfreeQueue(int capacity) {
		this.nodes = (T[]) new Object[capacity];
		this.head = new AtomicMarkableReference<Integer>(0, true);
		this.tail = new AtomicMarkableReference<Integer>(0, true);
	}

	public void enq(T t) throws InterruptedException {
		while(true) {
			boolean[] tailMark = {false};
			boolean[] headMark = {false};
			int tailLocal;
			do {
				tailLocal = tail.get(tailMark);
			} while(tailLocal - head.get(headMark) == nodes.length ||
					!headMark[0] || !tailMark[0]);
			int newTail = tailLocal + 1;
			if(tail.compareAndSet(tailLocal, newTail, true, false)) {
				nodes[tailLocal % nodes.length] = t;
				tail.attemptMark(newTail, true);
				break;
			}
		}
	}

	public T deq() throws InterruptedException{
		T val;
		while(true) {
			boolean[] tailMark = {false};
			boolean[] headMark = {false};
			int headLocal;
			do {
				headLocal = head.get(headMark);
			} while(tail.get(tailMark) - headLocal == 0 ||
					!tailMark[0] || !headMark[0]);
			int newHead = headLocal + 1;
			if(head.compareAndSet(headLocal, newHead, true, false)) {
				val = nodes[headLocal % nodes.length];
				head.attemptMark(newHead, true);
				break;
			}
		}
		return val;
	}
}
