/*
 * SimpleReadWriteLock.java
 * Copyright (C) 2015 Emiliano Firmino <emiliano.firmino@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class SimpleReadWriteLock implements ReadWriteLock {
    private int readers;
    private boolean writer;

    private Lock readLock;
    private Lock writeLock;

    private final Object condition = new Object();
    private final Object lock = new Object();

    public SimpleReadWriteLock() {
        this.readers = 0;
        this.writer  = false;

        this.readLock = new ReadLock();
        this.writeLock = new WriteLock();
    }

    public Lock readLock() {
        return readLock;
    }

    public Lock writeLock() {
        return writeLock;
    }

    private class WriteLock implements Lock {
        public void lock() {
            boolean acquired = false;

            for (;;) {
                synchronized (lock) {
                    acquired = readers == 0 && !writer;

                    if (acquired) {
                        writer = true;
                        break;
                    }
                }

                synchronized (condition) {
                    try { condition.wait(); }
                    catch (Exception e) {}
                }
            }
        }

        public void unlock() {
            synchronized (lock) {
                writer = false;
            }

            synchronized (condition) {
                condition.notifyAll();
            }
        }

        public void lockInterruptibly() {
            // not implemented
        }

        public boolean tryLock() {
            // not implemented
            return false;
        }

        public boolean tryLock(long time, TimeUnit unit) {
            // not implemented
            return false;
        }

        public Condition newCondition() {
            // not implemented
            return null;
        }
    }

    private class ReadLock implements Lock {
        public void lock() {
            for (;;) {
                synchronized (lock) {
                    if (!writer) {
                        readers++;
                        break;
                    }
                }

                synchronized (condition) {
                    try { condition.wait(); }
                    catch (Exception e) {}
                }
            }
        }

        public void unlock() {
            boolean notify = false;

            synchronized (lock) {
                readers--;

                if (readers == 0) {
                }
            }

            if (notify) {
                synchronized (condition) {
                    condition.notifyAll();
                }
            }
        }

        public void lockInterruptibly() {
            // not implemented
        }

        public boolean tryLock() {
            // not implemented
            return false;
        }

        public boolean tryLock(long time, TimeUnit unit) {
            // not implemented
            return false;
        }

        public Condition newCondition() {
            // not implemented
            return null;
        }
    }
}


