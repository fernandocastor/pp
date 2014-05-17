import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class SpinLocking {

    private static List<Counter> initializeCounters(int n, String type) {
        List<Counter> counters = new ArrayList<Counter>();
        for (int i = 0; i < n; i++) {
            Lock lock;
            if (type.equals("r"))
                lock = new ReentrantLock();
            else if (type.equals("a"))
                lock = new TASLock(TASLock.Type.AdditiveBackoff);
            else if (type.equals("e"))
                lock = new TASLock(TASLock.Type.ExponentialBackoff);
            else if (type.equals("q"))
                lock = new CLHQueueLock();
            else if (type.equals("q+"))
                lock = new CLHOptimizedQueueLock();
            else if (type.equals("s"))
                lock = new SafeLock();
            else
                lock = new TASLock(TASLock.Type.NoBackoff);

            counters.add(new Counter(lock));
        }
        return counters;
    }

    private static void runTimeBoundedTest(final int nthreads, List<Counter> counters,
                                           int minutes) throws InterruptedException {
        final List<CounterThread> threads = new ArrayList<CounterThread>();
        for (int i = 0; i < nthreads; i++) {
            CounterThread t = new CounterThread(counters);
            t.start();
            threads.add(t);
        }

        ScheduledExecutorService s = Executors.newScheduledThreadPool(1);
        s.schedule(new Runnable() {
            public void run() {
                for (int i = 0; i < nthreads; i++)
                    threads.get(i).finish();
            }
        }, minutes, TimeUnit.MINUTES);
        s.shutdown();

        for (int i = 0; i < nthreads; i++) {
            CounterThread t = threads.get(i);
            t.join();
            System.out.println("Thread " + i + ": " + t.getCount());
        }
    }

    private static void runExecutionBoundedTest(int nthreads, List<Counter> counters,
                                                int nexecutions) throws InterruptedException {
        final List<CounterThread> threads = new ArrayList<CounterThread>();
        for (int i = 0; i < nthreads; i++) {
            CounterThread t = new CounterThread(counters, nexecutions);
            t.start();
            threads.add(t);
        }

        for (int i = 0; i < nthreads; i++) {
            CounterThread t = threads.get(i);
            t.join();
        }
    }

    public static void main(String[] args) throws InterruptedException, NoSuchMethodException {
        if (args.length != 4) {
            System.out.println("Wrong usage! Try this: java SpinLocking <nthreads> <lock-type> <test-type> (<minutes>|<executions>)");
            return;
        }

        int ncounters = 10;
        int nthreads = Integer.parseInt(args[0]);
        String type = args[1];
        String test = args[2];

        List<Counter> counters = initializeCounters(ncounters, type);
        if (test.equals("time")) {
            int minutes = Integer.parseInt(args[3]);
            runTimeBoundedTest(nthreads, counters, minutes);
        } else {
            int nexecutions = Integer.parseInt(args[3]);
            runExecutionBoundedTest(nthreads, counters, nexecutions);
        }
    }
}
