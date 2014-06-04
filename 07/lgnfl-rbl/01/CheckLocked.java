
public class CheckLocked {
    public static void main(String[] args) throws InterruptedException, NoSuchMethodException {
        TASLock lock1 = new TASLock(TASLock.Type.ExponentialBackoff);
        System.out.println(lock1.isLocked());
        lock1.lock();
        try {
            System.out.println(lock1.isLocked());
        } finally {
            lock1.unlock();
        }
        System.out.println(lock1.isLocked());

        CLHOptimizedQueueLock lock2 = new CLHOptimizedQueueLock();
        System.out.println(lock2.isLocked());
        lock2.lock();
        try {
            System.out.println(lock2.isLocked());
        } finally {
            lock2.unlock();
        }
        System.out.println(lock2.isLocked());
    }
}
