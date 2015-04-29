import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Our Thread Safe Deque class.
 * @param <T> the type of boxed value
 */
public class SafeDeque<T> {
    private final AtomicInteger count = new  AtomicInteger();
    private final LinkedList<T> list = new LinkedList<T>();
    private final Lock lockLeft = new ReentrantLock();
    private final Lock lockRight = new ReentrantLock();

    private static final int SHARED_ITEM_LENGTH = 3;

    /**
     * Insert element at the begin
     */
    public void pushLeft(T element) {
        int n = count.getAndIncrement();

        if (n <= SHARED_ITEM_LENGTH) { lockRight.lock(); }
        lockLeft.lock();

        try {
            list.addFirst(element);
        } finally {
            lockLeft.unlock();
            if (n <= SHARED_ITEM_LENGTH) { lockRight.unlock(); }
        }
    }

    /**
     * Insert element at the end
     */
    public void pushRight(T element) {
        int n = count.getAndIncrement();

        lockRight.lock();
        if (n <= SHARED_ITEM_LENGTH) { lockLeft.lock(); }

        try {
            list.addLast(element);
        } finally {
            if (n <= SHARED_ITEM_LENGTH) { lockLeft.unlock(); }
            lockRight.unlock();
        }
    }

    /**
     * Remove element at the begin
     */
    public T popLeft() {
        int n = count.decrementAndGet();

        if (n < 0) {
            count.incrementAndGet();
            throw new NoSuchElementException("SafeDeque is empty");
        }

        if (n <= SHARED_ITEM_LENGTH) { lockRight.lock(); }
        lockLeft.lock();
        try {
            return list.removeFirst();
        } finally {
            lockLeft.unlock();
            if (n <= SHARED_ITEM_LENGTH) { lockRight.unlock(); }
        }
    }

    /**
     * Remove element at the end
     */
    public T popRight() {
        int n = count.decrementAndGet();

        if (n < 0) {
            count.incrementAndGet();
            throw new NoSuchElementException("SafeDeque is empty");
        }

        lockRight.lock();
        if (n <= SHARED_ITEM_LENGTH) { lockLeft.lock(); }
        try {
            return list.removeLast();
        } finally {
            if (n <= SHARED_ITEM_LENGTH) { lockLeft.unlock(); }
            lockRight.unlock();
        }
    }
}
