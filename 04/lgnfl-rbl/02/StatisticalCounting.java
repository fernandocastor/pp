import java.util.concurrent.atomic.*;
import java.util.ArrayList;
import java.util.List;

public class StatisticalCounting {

    private static class CounterThread extends Thread {
        private volatile boolean shouldStop = false;
        private AtomicInteger localCounter = new AtomicInteger(0);

        public long getLocalCounter() {
            return localCounter.get();
        }

        public void stopCounting() {
            shouldStop = true;
        }

        @Override
        public void run() {
            while (!shouldStop) {
                localCounter.incrementAndGet();
            }
        }
    }

    private static class ReaderThread extends Thread {
        private final List<CounterThread> counters;
        private final long k;

        public ReaderThread(List<CounterThread> counters, long k) {
            this.counters = counters;
            this.k = k;
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

                sum = 0;
                for (CounterThread t : counters)
                    sum += t.getLocalCounter();

                System.out.println("Current sum: " + sum);
            } while(sum < k);

            for (CounterThread t : counters)
                t.stopCounting();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.out.println("Wrong usage! Try this way: java StatisticalCounting <N> <K>");
            return;
        }

        int n = Integer.parseInt(args[0]);
        long k = Long.parseLong(args[1]);
        if (n <= 1 || k <= 1) {
            System.out.println("Bad arguments! Try N > 1 and K > 1. ");
            return;
        }

        List<CounterThread> counters = new ArrayList<CounterThread>();
        for (int i = 0; i < n; i++) {
            CounterThread t = new CounterThread();
            counters.add(t);
            t.start();
        }

        ReaderThread reader = new ReaderThread(counters, k);
        reader.start();
        reader.join();
    }
}
