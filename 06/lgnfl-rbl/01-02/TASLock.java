import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TASLock implements Lock {
    private class Backoff {
        private static final int MIN_DELAY = 10;
        private static final int MAX_DELAY = 100;

        private int limit;
        private final Random random;

        public Backoff() {
            limit = MIN_DELAY;
            random = new Random();
        }

        public void backoff() throws InterruptedException {
            int delay = random.nextInt(limit);
            updateLimit();
            Thread.sleep(delay);
        }

        private void updateLimit() {
            if (type == Type.AdditiveBackoff)
                limit = Math.min(MAX_DELAY, limit + limit/2);
            else
                limit = Math.min(MAX_DELAY, 2 * limit);
        }
    }

    public enum Type { NoBackoff, ExponentialBackoff, AdditiveBackoff}

    private final AtomicBoolean state = new AtomicBoolean(false);
    private final Type type;

    public TASLock(Type type) {
        this.type = type;
    }

    public void lock() {
        Backoff backoff = new Backoff();

        while (true) {
            while (state.get()) {};
            if (!state.getAndSet(true))
                return;

            if (type == Type.NoBackoff)
                continue;

            try {
                backoff.backoff();
            } catch (InterruptedException e) {}
        }
    }

    public void unlock() {
        state.set(false);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
