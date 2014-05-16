import java.util.Timer;
import java.util.TimerTask;

public class TAS {

    public static CounterThread[] threads;

    public static void stopAll() {
        System.out.println("stopAll callled");
        for (CounterThread thread : threads)
            thread.stopThread();
    }

    public enum LockType {
        NoBackoffCounterLock, QueueLock, ExponentialBackoffCounterLock, AdditiveBackoffCounterLock, ReentrantLock
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out
                    .println("Error: run as follows: TAS <number_of_threads> <number_of_counters> <time_in_ms> <count_limit_per_thread> [q, e, a, r]");
            System.exit(1);
        }

        int numberOfThreads = Integer.parseInt(args[0]);
        int numberOfCounters = Integer.parseInt(args[1]);
        int time = Integer.parseInt(args[2]);
        long countLimit = Integer.parseInt(args[3]);

        if (numberOfThreads <= 0 || numberOfCounters <= 0 || (time <= 0 && countLimit <= 0)) {
            System.out.println("Error: the number of threads, counters and time must be positive integers");
            System.exit(2);
        }

        LockType lockType = TAS.LockType.NoBackoffCounterLock;

        if (args.length > 4) {
            if (args[4].equals("q"))
                lockType = TAS.LockType.QueueLock;
            else if (args[4].equals("e"))
                lockType = TAS.LockType.ExponentialBackoffCounterLock;
            else if (args[4].equals("a"))
                lockType = TAS.LockType.AdditiveBackoffCounterLock;
            else if (args[4].equals("r"))
                lockType = TAS.LockType.ReentrantLock;
        }

        Counter[] counters = new Counter[numberOfCounters];
        TAS.threads = new CounterThread[numberOfThreads];

        System.out.println("Will start with " + numberOfCounters + " counters and " + numberOfThreads
                + " threads, with " + lockType + " lock.");

        for (int i = 0; i < numberOfCounters; ++i)
            counters[i] = new Counter(i, lockType);

        for (int i = 0; i < numberOfThreads; ++i)
            TAS.threads[i] = new CounterThread(counters, countLimit);

        for (CounterThread thread : threads)
            thread.start();

        System.out.println("Threads started");
        if (countLimit == 0) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    TAS.stopAll();
                }
            }, time);
            System.out.println("Timer scheduled");
        }

        for (CounterThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        long sum = 0;
        for (Counter counter : counters) {
            System.out.println("Counter " + counter.index + ", counted: " + counter.counter());
            sum += counter.counter();
        }

        for (CounterThread thread : threads) {
            System.out.println("CounterThread " + thread + ", counted: " + thread.counter());
        }

        System.out.println("Sum: " + sum);
        System.exit(0);
    }
}
