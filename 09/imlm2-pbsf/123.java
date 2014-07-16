package sandbox;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
* O algoritmo de fila é starvation free pois utiliza uma trava deadlock free e fair, além disso,
* o mesmo ordena as pessoas que querem alimentar e as que querem ser alimentadas utilizando uma política FIFO.
* Para garantir que sempre existem pessoas dispostas a alimentar/receber comida a implementação força cada pesssoa
* a alternar entre estar disponível alimentr/receber comida.
*/
public class DantesHell {

	private final SyncQueue<Food> queue = new StarvationFreeSyncQueue<>();
	private final Random random = new Random();

	public static void main(String[] args) throws InterruptedException {
		DantesHell dh = new DantesHell();
		List<PersonThread> persons = new ArrayList<>();
		boolean eatFirst = true;
		for(int i = 0; i < 5; i++) {
			PersonThread p = dh.new PersonThread(eatFirst);
			persons.add(p);
			eatFirst = !eatFirst;
		}
		for(PersonThread p : persons) {
			p.start();
		}
		Thread.sleep(5000);
		for (PersonThread personThread : persons) {
			personThread.interrupt();
		}
	}

	public enum Food {
		Fisch,
		Pasta,
		Rice,
		Steak,
		Lamb,
		Beans
	}

	public class PersonThread extends Thread {
		private boolean eat;

		public PersonThread(boolean eatFirst) {
			this.eat = eatFirst;
		}

		@Override
		public void run() {
			while(!this.isInterrupted()) {
				if(eat) {
					waitForFood();
				} else {
					feed();
				}
				eat = !eat;
			}
		}

		public void waitForFood() {
			Food food = queue.deq();
		}

		public void feed() {
			Food[] foods = Food.values();
			Food food = foods[random.nextInt(foods.length)];
			queue.enq(food);
		}
	}

	public static interface SyncQueue<T> {
		public void enq(T val);
		public T deq();
	}

	public static class StarvationFreeSyncQueue<T> implements SyncQueue<T> {

		ConcurrentLinkedQueue<T> aaa;
		private final Queue<Node<T>> nodes = new LinkedList<>();
		private final Lock lock = new ReentrantLock(true);
		private volatile Node<T> lastNode;

		@Override
		public void enq(T val) {
			lock.lock();
			try {
				if(nodes.isEmpty() || lastNode.type == NodeType.ITEM) {
					Condition unblockCondition = lock.newCondition();
					Node<T> node = new Node<>(unblockCondition, val, NodeType.ITEM);
					nodes.add(node);
					lastNode = node;
					unblockCondition.await();
				} else {
					Node<T> reservation = nodes.poll();
					reservation.value = val;
					reservation.unblockCondition.signal();
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} finally {
				lock.unlock();
			}
		}

		@Override
		public T deq() {
			T val;
			lock.lock();
			try {
				if(nodes.isEmpty() || lastNode.type == NodeType.RESERVATION) {
					Condition unblockCondition = lock.newCondition();
					Node<T> node = new Node<>(unblockCondition, null, NodeType.RESERVATION);
					nodes.add(node);
					lastNode = node;
					unblockCondition.await();
					val = node.value;
				} else {
					Node<T> item = nodes.poll();
					val = item.value;
					item.unblockCondition.signal();
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} finally {
				lock.unlock();
			}
			return val;
		}

		public static class Node<T> {
			private final Condition unblockCondition;
			private T value;
			private final NodeType type;
			public Node(Condition unblockCondition, T value, NodeType type) {
				super();
				this.unblockCondition = unblockCondition;
				this.value = value;
				this.type = type;
			}
		}
	}

	public static enum NodeType {
		ITEM,
		RESERVATION;
	}

	public static class Node<T> {
		private volatile NodeType type;
		private final AtomicReference<T> item;
		private final AtomicReference<Node<T>> next;
		public Node(T n, NodeType type) {
			this.type = type;
			this.item = new AtomicReference<>(n);
			this.next = new AtomicReference<Node<T>>(null);
		}
	}

	// Not really starvation free
	public static class SynchronousDualQueue<T> implements SyncQueue<T> {
		private volatile Node<T> sentinel = new Node<T>(null, NodeType.ITEM);
		private AtomicReference<Node<T>> head = new AtomicReference<>(sentinel);
		private AtomicReference<Node<T>> tail = new AtomicReference<>(sentinel);

		public void enq(T e) {
			Node<T> offer = new Node<T>(e, NodeType.ITEM);
			while(true) {
				Node<T> h = head.get(), t = tail.get();
				// If empty or there is one or more items (not reservations)
				if(h == t || t.type == NodeType.ITEM) {
					Node<T> n = t.next.get();
					if(t == tail.get()) {
						if(n != null) {
							tail.compareAndSet(t, offer);
						} else if(t.next.compareAndSet(n, offer)) {
							tail.compareAndSet(t, offer);
							while(offer.item.get() == e);
							h = head.get();
							if(offer == h.next.get()) {
								head.compareAndSet(h, offer);
							}
							return;
						}
					}
				} else {
					// The queue is not empty and contains reservations
					Node<T> n = h.next.get();
					if(t != tail.get() || h != head.get() || n == null) {
						continue;
					}
					boolean itemSet = n.item.compareAndSet(null, e);
					head.compareAndSet(h, n);
					if(itemSet) {
						return;
					}
				}	
			}
		}
		public T deq() {
			T val;
			Node<T> offer = new Node<T>(null, NodeType.RESERVATION);
			while(true) {
				Node<T> h = head.get(), t = tail.get();
				// If empty or if there is one or more reservations
				if(h == t || t.type == NodeType.RESERVATION) {
					Node<T> n = t.next.get();
					if(t == tail.get()) {
						if(n != null) {
							tail.compareAndSet(t, offer); // Helping out other thread
						} else if(t.next.compareAndSet(n /* null */, offer)) {
							tail.compareAndSet(t, offer);
							do {
								val = offer.item.get();
							} while(val == null);
							h = head.get();
							if(offer == h.next.get()) {
								head.compareAndSet(h, offer);
							}
							return val;
						}
					}
				} else {
					// The queue is not empty and contains actual items (not reservations)
					Node<T> n = h.next.get();
					if(t != tail.get() || h != head.get() || n == null) {
						continue;
					}
					val = n.item.get();
					boolean itemSet = n.item.compareAndSet(val, null);
					head.compareAndSet(h, n);
					if(itemSet) {
						return val;
					}
				}	
			}
		}
	}
}
