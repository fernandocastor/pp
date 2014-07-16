package trabalho9.ex121;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrayBoundedQueue<T> {

	private ReentrantLock tailLock, headLock;
	private Condition notEmptyCondition, notFullCondition;
	private AtomicInteger size;
	// private Node<T> head, tail;
	private volatile int head, tail;
	private Object[] array;
	private int capacity;

	public ArrayBoundedQueue(int _capacity) {

		array = new Object[_capacity];
		size = new AtomicInteger(0);
		capacity = _capacity;
		head = 0;
		tail = 0;
		tailLock = new ReentrantLock();
		notFullCondition = tailLock.newCondition();
		headLock = new ReentrantLock();
		notEmptyCondition = headLock.newCondition();

		// head = new Node<T>(null);
		// tail = head;
	}

	public void enq(T x) {
		boolean mustWakeDequeuers = false;
		headLock.lock();
		tailLock.lock();
		try
		{
			while (size.get() == capacity)
			{
				try
				{
					headLock.unlock();
					notFullCondition.await();
					headLock.lock();
					
				}
				catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}
			}
			if (head == tail && array[tail] == null)
			{
				array[tail] = x;
			}
			else
			{
				if (tail < capacity - 1)
				{
					tail++;
				}
				else
				{
					tail = 0;
				}
				array[tail] = x;

			}
			
			if (size.getAndIncrement() == 0)
			{
				mustWakeDequeuers = true;
			}
		}
		finally
		{
			tailLock.unlock();
			headLock.unlock();
		}

		if (mustWakeDequeuers)
		{
			headLock.lock();
			tailLock.lock();
			try
			{
				notEmptyCondition.signalAll();
			}
			finally
			{
				headLock.unlock();
				tailLock.unlock();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public T deq() {
		T result;
		boolean mustWakeEnqueuers = false;
		headLock.lock();
		tailLock.lock();
		try
		{
			while (size.get() == 0)
			{
				try
				{
					
					tailLock.unlock();
					notEmptyCondition.await();
					tailLock.lock();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}

			if(head == tail){
				result =  (T) array[tail];
				array[tail] = null;
			}
			else{
				result = (T) array[head];
				array[head] = null;
				if (head < capacity - 1){
					head++;
				}
				else{
					head=0;
				}
			}
			
			if (size.getAndDecrement() == capacity)
			{
				mustWakeEnqueuers = true;
			}
		}
		finally
		{
			headLock.unlock();
			tailLock.unlock();
		}

		if (mustWakeEnqueuers)
		{
			headLock.lock();
			tailLock.lock();
			
			try
			{
				notFullCondition.signalAll();
			}
			finally
			{
				headLock.unlock();
				tailLock.unlock();
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "ArrayBoundedQueue [array=" + Arrays.toString(array) + "]";
	}

	public static void main(String[] args) {

		final ArrayBoundedQueue<String> a = new ArrayBoundedQueue<>(3);

		System.out.println(a);

		Thread t1 = new Thread() {
			@Override
			public void run() {
				 a.enq("a");
				 System.out.println("t1 a => " + a);
				 a.enq("b");
				 System.out.println("t1 b => " + a);
				 a.enq("c");
				 System.out.println("t1 c => " + a);
				 a.enq("f");
				 System.out.println("t1 f => " + a);
			}
		};

		Thread t2 = new Thread() {
			@Override
			public void run() {
				a.deq();
				System.out.println(a);
				a.deq();
				a.deq();
				System.out.println(a);
				a.enq("d");
				System.out.println("t2 d => " + a);
				a.enq("e");
				System.out.println("t2 e => " + a);
			}
		};
		

		t1.start();
		t2.start();

	}
}