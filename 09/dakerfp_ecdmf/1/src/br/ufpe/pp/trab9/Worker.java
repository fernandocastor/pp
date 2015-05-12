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
    private Counter[]  counter;
    private Lock[]     lock;
    private XORShift32 random;

    private long myIncrement;

    public Worker(Counter[] counter, Lock[] lock) {
        this.random = new XORShift32(hashCode());
        this.counter = counter;
        this.lock = lock;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            int r = Math.abs(random.nextInt() % lock.length);

            Counter c = counter[r];
            Lock    l = lock[r];

            l.lock();
            try {
                c.increment();
                myIncrement++;
            } finally {
                l.unlock();
            }
        }
    }

    public long getMyIncrement() {
        return myIncrement;
    }
}


