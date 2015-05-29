/*
 * TTASLocker.java
 * Copyright (C) 2015
 * Daker Fernandes <dakerfp@gmail.com>
 * Emiliano Firmino <emiliano.firmino@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */

package br.ufpe.pp.trab9;

import br.ufpe.pp.trab9.XORShift32;

import java.lang.Math;

import java.util.concurrent.TimeUnit;

import java.util.concurrent.atomic.AtomicBoolean;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TTASLocker implements Lock {
    private static final int MIN_DELAY = 1 << 3;
    private static final int MAX_DELAY = 1 << 16;

    private AtomicBoolean state = new AtomicBoolean(false);

    private ThreadLocal<XORShift32> random = new ThreadLocal <XORShift32> () {
        protected XORShift32 initialValue() {
            return new XORShift32((int) Thread.currentThread().getId());
        }
    };

    public void lock() {
        boolean interrupted = false;

        int limit = MIN_DELAY;

        for(;;) {
            if (state.get()) {
                int delay = Math.abs(random.get().nextInt()) % limit;
                delay += MIN_DELAY;

                if (delay > MAX_DELAY)
                    delay = MAX_DELAY;

                try {
                    Thread.sleep(delay / 1000, delay % 1000);
                } catch (InterruptedException ie) {
                    interrupted = true;
                }

                //limit <<= 1;
                limit += 1000;
                if (limit > MAX_DELAY)
                    limit = MAX_DELAY;

                continue;
            }

            if (!state.getAndSet(true)) {
                break;
            }
        }

        if (interrupted)
            Thread.currentThread().interrupt();
    }

    public void unlock() {
        state.set(false);
    }

    public boolean tryLock() {
        return !state.getAndSet(true);
    }

    public boolean tryLock(long time, TimeUnit unit)
            throws InterruptedException {
        long start = System.nanoTime();
        long timeout = start + unit.toNanos(time);

        while (state.getAndSet(true)) {
            if (System.nanoTime() >= timeout) {
                return false;
            }
        }

        return true;
    }

    public void lockInterruptibly()
            throws InterruptedException {

        do {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Thread was interrupted");
            }
        } while(state.getAndSet(true));

    }

    public Condition newCondition() {
        throw new UnsupportedOperationException("Not Implemented");
    }
}


