import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedArrayQueue<T> {
    private final T[] data;
    private final int capacity;
    private AtomicInteger count;
    private volatile int headIndex;
    private volatile int tailIndex;

    private ReentrantLock enqLock;
    private ReentrantLock deqLock;
    private Condition notEmptyCondition;
    private Condition notFullCondition;

    public BoundedArrayQueue(int capacity) {
        this.capacity = capacity;
        data = (T[]) new Object[capacity];
        count = new AtomicInteger(0);

        enqLock = new ReentrantLock();
        notFullCondition = enqLock.newCondition();

        deqLock = new ReentrantLock();
        notEmptyCondition = deqLock.newCondition();
    }

    public void enq(T item) throws InterruptedException {
        boolean mustWakeDequeuers = false;
        enqLock.lock();
        try {
            while (count.get() == capacity)
                notFullCondition.await();

            data[tailIndex] = item;
            tailIndex = (tailIndex + 1) % capacity;

            if (count.getAndIncrement() == 0)
                mustWakeDequeuers = true;
        } finally {
            enqLock.unlock();
        }

        if (mustWakeDequeuers) {
            deqLock.lock();
            try {
                notEmptyCondition.signalAll();
            } finally {
                deqLock.unlock();
            }
        }
    }

    public T deq() throws InterruptedException {
        T result;
        boolean mustWakeEnqueuers = false;
        deqLock.lock();
        try {
            while (count.get() == 0)
                notFullCondition.await();

            result = data[headIndex];
            headIndex = (headIndex + 1) % capacity;

            if (count.getAndDecrement() == capacity)
                mustWakeEnqueuers = true;
        } finally {
            deqLock.unlock();
        }

        if (mustWakeEnqueuers) {
            enqLock.lock();
            try {
                notFullCondition.signalAll();
            } finally {
                enqLock.unlock();
            }
        }
        return result;
    }
}