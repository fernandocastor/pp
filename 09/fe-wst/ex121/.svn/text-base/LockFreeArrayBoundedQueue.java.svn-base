package trabalho9.ex121;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockFreeArrayBoundedQueue<T> {

	private ReentrantLock tailLock, headLock;
	private Condition notEmptyCondition, notFullCondition;
	private AtomicInteger size;
	// private Node<T> head, tail;
	private AtomicInteger head, tail;
	private AtomicInteger[] array;
	private int capacity;

	public LockFreeArrayBoundedQueue(int _capacity) {

		array = new AtomicInteger[_capacity];
		size = new AtomicInteger(0);
		capacity = _capacity;
		head = new AtomicInteger(0);
		tail = new AtomicInteger(-1);
		tailLock = new ReentrantLock();
		notFullCondition = tailLock.newCondition();
		headLock = new ReentrantLock();
		notEmptyCondition = headLock.newCondition();

		this.iniciarArray();
		
		// head = new Node<T>(null);
		// tail = head;
	}
	
	public void iniciarArray(){
		for (int i = 0; i < capacity; i++)
		{
			array[i]=new AtomicInteger(-1);
		}
	}

	public void enq(AtomicInteger value) {
		// Node node = new Node(value);
		while (true)
		{
			AtomicInteger last = array[(tail.get()==-1?0:tail.get())];
			AtomicInteger next = array[(tail.get() + 1 >= capacity ? 0 : tail.get() + 1)];

			if (last == array[(tail.get()==-1?0:tail.get())])
			{

				if (next.get() == -1)
				{
					if (array[(tail.get() + 1 >= capacity ? 0 : tail.get() + 1)].compareAndSet(next.get(), value.get()))
					{
						tail.incrementAndGet();
						return;
					}
				}
			}
		}
	}

	public T deq() {
		//a fazer
		return null;
	}
	
	@Override
	public String toString() {
		return "LockFreeArrayBoundedQueue [array=" + Arrays.toString(array) + "]";
	}

	public static void main(String[] args) {

		final LockFreeArrayBoundedQueue<String> a = new LockFreeArrayBoundedQueue<>(10);

		System.out.println(a);
		
		Thread t1 = new Thread() {
			@Override
			public void run() {
				a.enq(new AtomicInteger(1));
				System.out.println("t1 a => " + a);
				a.enq(new AtomicInteger(2));
				System.out.println("t1 a => " + a);
				a.enq(new AtomicInteger(3));
				System.out.println("t1 a => " + a);
				a.enq(new AtomicInteger(4));
				System.out.println("t1 a => " + a);
				a.enq(new AtomicInteger(5));
				System.out.println("t1 a => " + a);
//				a.enq("b");
//				System.out.println("t1 b => " + a);
//				a.enq("c");
//				System.out.println("t1 c => " + a);
//				a.enq("f");
//				System.out.println("t1 f => " + a);
			}
		};

		Thread t2 = new Thread() {
			@Override
			public void run() {
				
				a.enq(new AtomicInteger(6));
				System.out.println("t2 a => " + a);
				a.enq(new AtomicInteger(7));
				System.out.println("t2 a => " + a);
				a.enq(new AtomicInteger(8));
				System.out.println("t2 a => " + a);
				a.enq(new AtomicInteger(9));
				System.out.println("t2 a => " + a);
				a.enq(new AtomicInteger(10));
				System.out.println("t2 a => " + a);
				
//				a.deq();
//				System.out.println(a);
//				a.deq();
//				a.deq();
//				System.out.println(a);
//				a.enq("d");
//				System.out.println("t2 d => " + a);
//				a.enq("e");
//				System.out.println("t2 e => " + a);
			}
		};

		t1.start();
		t2.start();

	}
}