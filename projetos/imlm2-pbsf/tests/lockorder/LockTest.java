package tests;

import lockorder.checker.quals.*;

import java.util.concurrent.locks.*;

public class LockTest {

    // Suppressing cycle detection warnings for local variables
    @SuppressWarnings("local.cycle.detection.warning")
    void m1() {
        //Assignments below are allowed because RHS is @LockFree.
        @LockOrder(before={}) Lock a = new ReentrantLock();
        @LockOrder(before={"a"}) Lock c = new ReentrantLock();
        @LockOrder(before={"a", "c"}) Lock b = new ReentrantLock();
        // Order to obtain locks: b -> c -> a
        b.lock();
        c.lock();
        a.lock();

        a.unlock();
        c.unlock();
        b.unlock();

        a.lock();
        //::error: (wrong.lock.order)
        c.lock();
        //::error: (wrong.lock.order)
        b.lock();

        a.unlock();
        c.unlock();
        b.unlock();

        a.lock();
        //::error: (wrong.lock.order)
        b.lock();
        //::error: (wrong.lock.order)
        c.lock();

        a.unlock();
        c.unlock();
        b.unlock();
    }
}