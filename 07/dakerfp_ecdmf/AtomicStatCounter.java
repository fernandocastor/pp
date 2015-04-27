import java.util.concurrent.atomic.AtomicLong;
public class AtomicStatCounter
{
    public void runUntil(final int n, final int k) {
        CounterThread[] threads = new CounterThread[n];
        for (int i = 0; i < n; i++) {
            threads[i] = new CounterThread();
            threads[i].start();
        }

        long sum = 0;
        for (;;) {
            sum = 0;
            for (int i = 0; i < n; i++) {
                sum += threads[i].getCounter();
            }

            System.out.println(sum);
            if (sum >= k) {
                break;
            }

        }

        for (int i = 0; i < n; i++) {
            threads[i].stop();
        }

        System.out.println("Stopped with " + sum + " of " + k);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("usage: java StatCounter N K");
            System.out.println("Where N is the number of count threads and K the stop point");
        }

        AtomicStatCounter statCounter = new AtomicStatCounter();
        statCounter.runUntil(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

    private class CounterThread extends Thread {
        //private long p1, p2, p3, p4, p5, p6, p7; // cache line padding
        private AtomicLong counter = new AtomicLong();
        //private long p8, p9, p10, p11, p12, p13, p14; // cache line padding

        public void run() {
            for(;;) {
                counter.incrementAndGet();
            }
        }

        public long getCounter() {
            return counter.get();
        }
    }
}


