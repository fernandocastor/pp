import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 *@author  tmbs2
 *
 */
public class HashingDequeWithLinkedList<T> {

	private LinkedList<T> deque = new LinkedList<T>();
	private Map<Integer, Object> hash = new HashMap<Integer, Object>();

	//locks to remove items
	private ReentrantLock leftReentratLock = new ReentrantLock();
	private ReentrantLock rightReentratLock = new ReentrantLock();

	//locks to add items
	private Object lefMutex = new Object();
	private Object rightMutex = new Object();

	//locks to 
	private volatile Object[] indexLock;

	private volatile int listIndex = 0;

	public void pushLeft(T item, String threadName) {
		
		synchronized (lefMutex) {
			this.deque.addFirst(item);
			addHashDequeItem(lefMutex);
			printExecutionLog("Push Left", threadName, hash.get(listIndex), deque.get(listIndex));
		}

	}

	public void pushRight(T item, String threadName) {

		synchronized (rightMutex) {
			this.deque.addLast(item);
			addHashDequeItem(rightMutex);
			printExecutionLog("Push Right", threadName, hash.get(listIndex), deque.get(listIndex));
		}
	}

	public void popLeft(String threadName) throws InterruptedException {

		while (!isEmpty() && leftReentratLock.tryLock()) {
			leftReentratLock.lock();
			blockAccessOtherEnd(rightReentratLock);
			try {
				this.deque.removeFirst();
				this.removeHashDequeItem(leftReentratLock);
				//	printExecutionLog("Pop Left", threadName);
			} finally {
				this.leftReentratLock.unlock();
				unblockAccessOtherEnd(rightReentratLock);
			}
		}

	}

	public void popRight(String threadName) throws InterruptedException {
		
		while (!isEmpty() && rightReentratLock.tryLock()) {
			rightReentratLock.lock();
			blockAccessOtherEnd(leftReentratLock);
			try {
				this.deque.removeLast();
				this.removeHashDequeItem(rightReentratLock);
				//	printExecutionLog("Pop Right", threadName);
			} finally {
				this.rightReentratLock.unlock();
				unblockAccessOtherEnd(leftReentratLock);
			}
		}

	}

	private boolean isEmpty() {
		return (deque.size() == 0);
	}

	private void printExecutionLog(String action, String threadName, Object object, T t) {
		System.out.println("<---Thread Name: " + threadName + " -> " + action);
		System.out.println("<---Thread Name: " + threadName + " -> " + "Hash Index: " + object);
		System.out.println("<---Thread Name: " + threadName + " -> " + "Deque Position: " + t);
		System.out.println("<---Thread Name: " + threadName + " -> " + "Deque Current Size: " + deque.size());
		System.out.println("<------------------------------------------------------------------------>");

	}

	private void blockAccessOtherEnd(Lock lock) {
		lock.lock();
	}

	private void unblockAccessOtherEnd(Lock lock) {
		lock.unlock();
	}

	private void addHashDequeItem(Object lock) {
		
		synchronized (lock) {
			indexLock[listIndex] = new Object();
			hash.put(listIndex, indexLock[listIndex]);
			listIndex++;
		}

	}

	private void removeHashDequeItem(Object lock) {
		
		synchronized (lock) {
			Arrays.asList(indexLock).remove(listIndex);
			hash.remove(listIndex);
			listIndex--;
		}

	}

}
