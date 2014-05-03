import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.ArrayList;
import java.util.List;

public class ExactLimitCounting {
    static long globalcountmax = 10000;
    static long globalcount = 0;
    static long globalreserve = 0;
    static int MAX_COUNTERMAX = (1 << 16) - 1;

    static List<CounterThread> counters;
    static int N;

    public static class Pair {
        public int a,b;
        public Pair(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    private static class CounterThread extends Thread {
        private volatile boolean shouldStop = false;
        public AtomicInteger ctrandmax = new AtomicInteger(0);

        public int getLocalCounter() {
            int cami = ctrandmax.get(); // atomic read
            return (cami >> 16) & MAX_COUNTERMAX;
        }

        public Pair split_ctrandmax_int(int cami) {
            return new Pair((cami >> 16) & MAX_COUNTERMAX, cami & MAX_COUNTERMAX);
        }

        static int merge_ctrandmax(int c, int cm) {
            return (c << 16) | cm;
        }

        public void stopCounting() {
            shouldStop = true;
        }

        public void globalize_count() {
            int oldValue = ctrandmax.get();
            Pair x = split_ctrandmax_int(oldValue);
            globalcount += x.a;
            globalreserve -= x.b;
            ctrandmax.set(0); // atomic set
        }

        public void flush_local_count() {
            if (globalreserve == 0)
                return;
            for (CounterThread t : counters) {
                int oldValue = t.ctrandmax.getAndSet(0);
                Pair x = split_ctrandmax_int(oldValue);
                globalcount += x.a;
                globalreserve -= x.b;
            }
        }

        public void balance_count() {
            int c, cm, oldValue;
            long limit;
            limit = globalcountmax - globalcount - globalreserve;
            limit /= N;
            if (limit > MAX_COUNTERMAX) {
                cm = MAX_COUNTERMAX;
            } else {
                cm = (int) limit;
            }
            globalreserve += cm;
            c = 0;
            oldValue = merge_ctrandmax(c, cm);
            ctrandmax.set(oldValue); // atomic set
        }

        private int add_count(int delta) {
            int oldValue, newValue;
            boolean slowPath = false;
            do {
                oldValue = ctrandmax.get();
                Pair x = split_ctrandmax_int(oldValue);
                if (delta > MAX_COUNTERMAX || x.a + delta > x.b) {
                    slowPath = true;
                    break;
                }
                newValue = merge_ctrandmax(x.a + delta, x.b);
            } while (!ctrandmax.compareAndSet(oldValue, newValue));

            if (!slowPath)
                return 1;

            synchronized (ExactLimitCounting.class) {
                globalize_count();
                if (globalcountmax - globalcount - globalreserve < delta) {
                    flush_local_count();
                    if (globalcountmax - globalcount - globalreserve < delta) {
                        return 0;
                    }
                }
                globalcount += delta;
                balance_count();
                return 1;
            }
        }

        @Override
        public void run() {
            while (!shouldStop) {
                add_count(1);
            }
        }
    }

    private static class ReaderThread extends Thread {

        public ReaderThread() {
        }

        private long read_count() {
            Integer c, cm, oldValue;
            long sum;

            synchronized (ExactLimitCounting.class) {
                sum = globalcount;
                for (CounterThread t : counters) {
                    sum += t.getLocalCounter();
                }
                return sum;
            }
        }

        @Override
        public void run() {
            long sum = 0;
            do {
                try {
                    Thread.sleep(10);
                } catch (Throwable t) {
                    System.out.println("Error on reader thread!");
                    break;
                }

                sum = read_count();

                System.out.println("Current sum: " + sum);
            } while(sum < globalcountmax);

            for (CounterThread t : counters)
                t.stopCounting();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.out.println("Wrong usage! Try this way: java ExactLimitCounting <N> <K>");
            return;
        }

        int n = Integer.parseInt(args[0]);
        long k = Long.parseLong(args[1]);
        if (n <= 1 || k <= 1) {
            System.out.println("Bad arguments! Try N > 1 and K > 1. ");
            return;
        }
        N = n;
        globalcountmax = k;

        counters = new ArrayList<CounterThread>();
        for (int i = 0; i < n; i++) {
            CounterThread t = new CounterThread();
            counters.add(t);
        }
        for (CounterThread t : counters) {
            t.start();
        }

        ReaderThread reader = new ReaderThread();
        reader.start();
        reader.join();
    }
}
