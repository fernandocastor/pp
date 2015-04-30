import java.util.LinkedList;
import java.util.Vector;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Our Thread Safe HashDeque class.
 * @param <T> the type of boxed value
 */
public class HashDeque<T> {
    private class AtomicWrappingCounter {
        private AtomicInteger value;
        private final int max;

        public AtomicWrappingCounter(int start, int max) {
            this.value = new AtomicInteger(start);
            this.max = max;
        }

        public int get() {
            return value.get();
        }

        /* Simple modification of AtomicInteger.incrementAndGet() */
        public int incrementAndGet() {
            for (;;) {
                int current = get();
                int next = (current + 1) % max;
                if (value.compareAndSet(current, next))
                    return next;
            }
        }

        public int decrementAndGet() {
            for (;;) {
                int current = get();
                int next = (current - 1 + max) % max;
                if (value.compareAndSet(current, next))
                    return next;
            }
        }

        public int getAndIncrement() {
            for (;;) {
                int current = get();
                int next = (current + 1) % max;
                if (value.compareAndSet(current, next))
                    return current;
            }
        }

        public int getAndDecrement() {
            for (;;) {
                int current = get();
                int next = (current - 1 + max) % max;
                if (value.compareAndSet(current, next))
                    return current;
            }
        }
    }

    private AtomicWrappingCounter leftHead;
    private AtomicWrappingCounter rightHead;
    private Vector<LinkedList<T>> lists;
    private Lock[] locks;

    public HashDeque(int hashSize) {
        leftHead = new  AtomicWrappingCounter(0, hashSize);
        rightHead = new  AtomicWrappingCounter(1, hashSize);
        lists = new Vector<LinkedList<T>>(hashSize);
        locks = new Lock[hashSize];
        for (int i =0; i < hashSize; i++) {
            lists.add(i, new LinkedList<T>());
            locks[i] = new ReentrantLock();
        }
    }

    /**
     * Insert element at the begin
     */
    public void pushLeft(T element) {
        int left = leftHead.getAndDecrement();
        Lock lock = locks[left];
        lock.lock();
        lists.get(left).addFirst(element);
        lock.unlock();
    }

    /**
     * Insert element at the end
     */
    public void pushRight(T element) {
        int right = rightHead.getAndIncrement();
        Lock lock = locks[right];
        lock.lock();
        lists.get(right).addLast(element);
        lock.unlock();
    }

    /**
     * Remove element at the begin
     */
    public T popLeft() {
        int left = leftHead.incrementAndGet();
        Lock lock = locks[left];
        lock.lock();
        try {
            return lists.get(left).removeFirst();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Remove element at the end
     */
    public T popRight() {
        int right = rightHead.decrementAndGet();
        Lock lock = locks[right];
        lock.lock();
        try {
            return lists.get(right).removeLast();
        } finally {
            lock.unlock();
        }
    }
}
