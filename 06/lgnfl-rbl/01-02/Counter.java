import java.util.concurrent.locks.Lock;

public class Counter {
    private long counter = 0;
    private Lock lock;

    public Counter(Lock lock) {
        this.lock = lock;
    }

    public void increment() {
        lock.lock();
        counter++;
        lock.unlock();
    }

    public long getCount() {
        return counter;
    }
}
