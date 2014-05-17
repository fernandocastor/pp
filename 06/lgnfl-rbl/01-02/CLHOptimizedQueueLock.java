import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

public class CLHOptimizedQueueLock implements Lock {
    private static class Node {
        private volatile Thread thread;
        private volatile Node prev;

        Node() {
            thread = Thread.currentThread();
            prev = null;
        }
        public boolean isHead() {
            return prev == null;
        }
    }

    AtomicReference<Node> tail;
    AtomicBoolean locked;
    ThreadLocal<Node> next;

    public CLHOptimizedQueueLock() {
        tail = new AtomicReference<Node>();
        locked = new AtomicBoolean(false);
        next = new ThreadLocal<Node>() {
            protected Node initialValue() {
                return null;
            }
        };
    }

    private Node enq() {
        Node node = new Node();
        while (true) {
            Node pred = tail.get();
            node.prev = pred;
            if (tail.compareAndSet(pred, node)) {
                break;
            }
        }
        return node;
    }


    public void lock() {
        Node node = enq();
        boolean interrupted = false;
        for (;;) {
            if (node.isHead() && !locked.getAndSet(true)) {
                popHead();
                if (interrupted)
                    Thread.currentThread().interrupt();
                return;
            } else {
                LockSupport.parkNanos(this, 1000);
                interrupted |= Thread.interrupted();
            }
        }
    }

    public void unlock() {
        Node successor = next.get();
        if (successor == null) {
            successor = getHead();
        }
        locked.set(false);
        if (successor != null) {
            LockSupport.unpark(successor.thread);
        }
    }

    private Node getHead() {
        Node n = null;
        for (n = tail.get(); n != null; n = n.prev) {
            if (n.prev == null)
                break;
        }
        return n;
    }

    private Node popHead() {
        Node n = null;
        while (true) {
            Node successor = null;
            for (n = tail.get(); n != null; n = n.prev) {
                if (n.prev == null)
                    break;
                successor = n;
            }
            next.set(successor); // optimization for unlock
            if (successor != null) {
                // if there is a successor, then clearing its flag is sufficient
                // to pop current head and let it be GCed
                successor.prev = null;
                break;
            } else {
                // otherwise, it will be trickier. we need to set tail
                // to null since this is the only node on the queue
                // if we fail, we must try again to find the successor
                // but then we should succeed next time as we go 1st case
                Node t = tail.get();
                if (t == n && tail.compareAndSet(t, null))
                    break;
            }
        }
        return n;
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
