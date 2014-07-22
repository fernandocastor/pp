package TinyTM.contention;

import TinyTM.Transaction;

public class KarmaManager extends ContentionManager {

    BackoffManager backoffManager = new BackoffManager();

    @Override
    public void resolve(Transaction me, Transaction other) {
        long delta = other.getPriority() - me.getPriority();
        if (me.getPriority() > delta)
            other.abort();
        else
            backoffManager.resolve(me, other);
    }
}
