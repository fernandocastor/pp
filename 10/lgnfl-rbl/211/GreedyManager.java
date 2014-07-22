package TinyTM.contention;

import TinyTM.Transaction;

import java.util.Random;

public class GreedyManager extends ContentionManager {

    private static final int DELAY = 1024;
    private Random random = new Random();

    @Override
    public void resolve(Transaction me, Transaction other) {
        if (me.getTimestamp() <= other.getTimestamp() || other.isWaiting()) {
            other.abort();
        } else {
            me.setWaiting(true);
            try {
                Thread.sleep(random.nextInt(DELAY));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } finally {
                me.setWaiting(false);
            }
        }
    }
}
