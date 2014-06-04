import java.util.concurrent.locks.Lock;

public class Counter {
    private long counter = 0;

    public Counter() {}

    public void increment() {
        counter++;
    }

    public long getCount() {
        return counter;
    }
}
