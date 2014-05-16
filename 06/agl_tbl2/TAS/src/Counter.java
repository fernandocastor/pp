import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private long mCounter = 0;
    public final Lock lock;
    public int index;

    public Counter(int index, TAS.LockType lock) {
        this.index = index;
        this.lock = lock == TAS.LockType.ReentrantLock ? new ReentrantLock()
                : (lock == TAS.LockType.QueueLock ? new QueueLock() : new CounterLock(lock));
    }

    public void increment() {
        ++mCounter;
    }

    public long counter() {
        return mCounter;
    }
}
