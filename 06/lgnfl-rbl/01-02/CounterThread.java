import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CounterThread extends Thread {

    private final List<Counter> counters;
    private long count = 0;
    private final long countMax;
    private final Random rand = new Random();
    private volatile boolean running;

    public CounterThread(List<Counter> counters) {
        this.counters = counters;
        this.countMax = -1;
        this.running = true;
    }

    public CounterThread(List<Counter> counters, long countMax) {
        this.counters = counters;
        this.countMax = countMax;
        this.running = false;
    }

    public void finish() {
        running = false;
    }

    public long getCount() {
        return count;
    }

    @Override
    public void run() {
        while (running || (countMax > 0 && count < countMax)) {
            int index = rand.nextInt(counters.size());
            counters.get(index).increment();
            count++;
        }
    }
}
