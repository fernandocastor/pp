/*
 * Main.java
 * Copyright (C) 2015
 * Daker Fernandes <dakerfp@gmail.com>
 * Emiliano Firmino <emiliano.firmino@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */

package br.ufpe.pp.trab9;

import br.ufpe.pp.trab9.TASLocker;
import br.ufpe.pp.trab9.Counter;
import br.ufpe.pp.trab9.Worker;

import java.util.concurrent.locks.Lock;

public class Main
{
    public static void main(String[] args) {
        int runDuration = args.length > 0 ? Integer.parseInt(args[0]) : 5;
        int numThreads = args.length > 1 ? Integer.parseInt(args[1]) : 10;

        Counter   counter = new Counter();
        // TASLocker locker  = new TASLocker();
        // ReentrantLock locker = new ReentrantLock();
        TTASLocker locker  = new TTASLocker();

        Worker[] worker = new Worker[numThreads];

        for (int i = 0; i < worker.length; i++) {
            worker[i] = new Worker(counter, locker);
        }

        for (int i = 0; i < worker.length; i++) {
            worker[i].start();
        }

        try {
            Thread.sleep(runDuration * 1000);
        } catch (InterruptedException ie) {
            System.exit(-1);
        }

        for (int i = 0; i < worker.length; i++) {
            worker[i].interrupt();
        }

        for (int i = 0; i < worker.length; i++) {
            try {
                worker[i].join();
            } catch (InterruptedException ie) {
                // IGNORED
            }
        }

        System.out.println("Counter counted to " + counter.get());

        for (int i = 0; i < worker.length; i++) {
            System.out.println("Worker " + i + " counted to " + worker[i].getMyIncrement());
        }

    }

}


