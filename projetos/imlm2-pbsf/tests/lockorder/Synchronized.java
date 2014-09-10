package tests;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lockorder.checker.quals.*;

public class Synchronized {
  //::error: (lock.order.cycle)
    @LockOrder(before={"bb"}) Object aa = new Object();
  //::error: (lock.order.cycle)
    @LockOrder(before={"cc"}) Object bb = new Object();
  //::error: (lock.order.cycle)
    @LockOrder(before={"aa"}) Object cc = new Object();

    void foo() {
        synchronized (aa) {
            synchronized (bb) {
                
            }
        }
    }

    void bar() {
        synchronized (bb) {
            synchronized (cc) {
            }
        }
    }

    void foobar() {
        synchronized (cc) {
            synchronized (aa) {
                
            }
        }
    }

    // Suppressing cycle detection warnings for local variables
    @SuppressWarnings("local.cycle.detection.warning")
    void m1() {
        //Assignments below are allowed because RHS is @LockFree.
        @LockOrder(before={}) Object a = new Object();
        @LockOrder(before={"a"}) Object c = new Object();
        @LockOrder(before={"a", "c"}) Object b = new Object();
        // Order to obtain locks: b -> c -> a
        
        synchronized(b) {
            synchronized(c) {
                synchronized(a) {
                    
                }
            }
        }
        
        synchronized(a) {
            //::error: (wrong.lock.order)
            synchronized(b) {
                //::error: (wrong.lock.order)
                synchronized(c) {
                    
                }
            }
        }
        synchronized(a) {
          //::error: (wrong.lock.order)
            synchronized(c) {
                //::error: (wrong.lock.order)
                synchronized(b) {
                    
                }
            }
        }

    }
}