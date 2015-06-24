/*
 * CountDownLatch.java
 * Copyright (C) 2015 Emiliano Firmino <emiliano.firmino@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */
import java.util.concurrent.atomic.AtomicInteger;

public class CountDownLatch {
    private AtomicInteger counter;
    private Object condition = new Object();

    public CountDownLatch(int n) {
        counter = new AtomicInteger(n);
    }

    public void countDown() {
        int n = counter.decrementAndGet();
        if (n == 0) {
            synchronized (condition) {
                condition.notifyAll();
            }
        }
    }

    public void await() {
        while (counter.get() > 0) {
            synchronized (condition) {
                try { condition.wait(); } catch (InterruptedException ie) {}
            }
        }
    }
}


