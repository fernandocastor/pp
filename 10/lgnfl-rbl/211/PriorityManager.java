package TinyTM.contention;

import TinyTM.Transaction;

import java.util.Random;

public class PriorityManager extends ContentionManager {

    private static final int DELAY = 1024;
    private Random random = new Random();

    @Override
    public void resolve(Transaction me, Transaction other) {
        if (me.getTimestamp() <= other.getTimestamp()) {
            other.abort();
        } else {
            try {
                Thread.sleep(random.nextInt(DELAY));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
