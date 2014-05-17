import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CLHQueueLock implements Lock {
    private static class Node {
        private volatile boolean locked = false;
    }

    AtomicReference<Node> tail = new AtomicReference<Node>(new Node());
    ThreadLocal<Node> myPred;
    ThreadLocal<Node> myNode;

    public CLHQueueLock() {
        tail = new AtomicReference<Node>(new Node());
        myNode = new ThreadLocal<Node>() {
            protected Node initialValue() {
                return new Node();
            }
        };
        myPred = new ThreadLocal<Node>() {
            protected Node initialValue() {
                return null;
            }
        };

    }

    public void lock() {
        Node node = myNode.get();
        node.locked = true;
        Node pred = tail.getAndSet(node);
        myPred.set(pred);
        while (pred.locked) {}
    }

    public void unlock() {
        Node node = myNode.get();
        node.locked = false;
        myNode.set(myPred.get());
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
