import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;

public class SafeLock implements Lock {
    //static AtomicInteger safeLockID = new AtomicInteger(0);
    static ConcurrentHashMap<Thread, LinkedList<SafeLock>> ownedLocks = new ConcurrentHashMap<Thread, LinkedList<SafeLock>>();
    static LinkedList<SafeLock> getOwnedLocks() {
        LinkedList<SafeLock> al = ownedLocks.get(Thread.currentThread());
        if(al == null) {
            al = new LinkedList<SafeLock>();
            ownedLocks.put(Thread.currentThread(), al);
        }
        return al;        
    }

    // Adds a certain lock to the current thread list of owned locks
    static void addOwnedLock(LinkedList<SafeLock> al, SafeLock l) {
        al.addFirst(l);
    }
    
    // Checks if a given thread owns a certain lock
    static boolean isOwnedLock(LinkedList<SafeLock> al, Thread t, SafeLock l) {
        if (al != null) {
            Iterator<SafeLock> iter = al.iterator();
            while(iter.hasNext()) {
                SafeLock current = iter.next();
                if (l == current) {
                    return true;
                }
            }
        }
        return false;
    }

    // Removes a certain lock from the current thread list of owned locks
    static void removeOwnedLock(LinkedList<SafeLock> al, SafeLock l) {
        if (al != null) {
            for (int i = 0; i < al.size(); i++) {
                SafeLock current = al.get(i);
                if (l == current) {
                    al.remove(i);
                    break;
                }
            }
        }
    }
    
    // Checks if a given thread is waiting on a lock owned by the current thread
    static boolean waitingOnOwnedLock(LinkedList<SafeLock> al, Thread owner) {
        if (al != null) {
            Iterator<SafeLock> iter = al.iterator();
            while(iter.hasNext()) {
                SafeLock l = iter.next();
                for (Node s = l.tail.get(); s != null; s = s.prev) {
                    if (s.thread == owner)
                        return true;
                }
            }
        }
        return false;
    }


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

    public AtomicReference<Node> tail;
    public AtomicBoolean locked;
    public ThreadLocal<Node> next;
    public volatile Thread owner;
    //public int lockID;

    public SafeLock() {
        tail = new AtomicReference<Node>(null);
        locked = new AtomicBoolean(false);
        next = new ThreadLocal<Node>() {
            protected Node initialValue() {
                return null;
            }
        };
        owner = null;
        //lockID = safeLockID.getAndIncrement();
    }

    public Thread getOwner() {
        return locked.get() ? owner : null;
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
        LinkedList<SafeLock> ownedLocks = getOwnedLocks();

        Node node = enq();
        boolean interrupted = false;
        for (;;) {
            if (node.isHead() && !locked.getAndSet(true)) {
                // lock acquire successful
                owner = Thread.currentThread();
                addOwnedLock(ownedLocks, this);
                popHead();
                //System.out.println("SafeLock.lock() " + Thread.currentThread() + " acquired " + lockID);

                if (interrupted)
                    Thread.currentThread().interrupt();
                return;
            } else {
                //System.out.println("SafeLock.lock() " + Thread.currentThread() + " failed " + lockID);
                // lock acquire about to interrupt
                Thread owner = this.getOwner();

                // other thread owns the lock I want, so check whether I own any locks that it wants
                if(waitingOnOwnedLock(ownedLocks, owner)) {
                    throw new DeadlockException();
                }

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
        owner = null;
        locked.set(false);
        removeOwnedLock(getOwnedLocks(), this);
        //System.out.println("SafeLock.unlock() " + Thread.currentThread() + " released " + lockID);
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
