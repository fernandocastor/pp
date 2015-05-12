/*
 * Worker.java
 * Copyright (C) 2015 Emiliano Firmino <emiliano.firmino@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */

package br.ufpe.pp.trab9;

import br.ufpe.pp.trab9.Counter;
import java.util.concurrent.locks.Lock;

public class Worker extends Thread {
    private Counter counter;
    private Lock lock;

    private long myIncrement;

    public Worker(Counter counter, Lock lock) {
        this.counter = counter;
        this.lock = lock;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            lock.lock();
            try {
                counter.increment();
                myIncrement++;
            } finally {
                lock.unlock();
            }

            if (myIncrement >= 1000) {
                return;
            }
        }
    }

    public long getMyIncrement() {
        return myIncrement;
    }
}


