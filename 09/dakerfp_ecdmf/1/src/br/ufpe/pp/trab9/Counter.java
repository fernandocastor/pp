/*
 * Counter.java
 * Copyright (C) 2015 Emiliano Firmino <emiliano.firmino@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */
package br.ufpe.pp.trab9;

public class Counter
{
    private long count = 0;

    public Counter() {

    }

    public void increment() {
        this.count++;
    }

    public void reset() {
        this.count = 0;
    }

    public long get() {
        return count;
    }
}


