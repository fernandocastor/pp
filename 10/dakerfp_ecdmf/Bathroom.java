/*
 * Bathroom.java
 * Copyright (C) 2015 Emiliano Firmino <emiliano.firmino@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Bathroom {
    private ReentrantLock lock = new ReentrantLock(true);
    private Condition condition = lock.newCondition();

    private AtomicInteger current = new AtomicInteger(0);
    private AtomicInteger counter = new AtomicInteger(0);

    public void enterMale() {
        lock();
    }

    public void leaveMale() {
        unlock();
    }

    public void enterFemale() {
        lock();
    }

    public void leaveFemale() {
        unlock();
    }

    private void lock() {
        int ticket = counter.getAndIncrement();

        while (ticket != current.get()) {
            try { condition.await(); } catch (InterruptedException ie) {}
        }

        lock.lock();
    }

    private void unlock() {
        current.getAndIncrement();
        lock.unlock();
        condition.signalAll();
    }
}


