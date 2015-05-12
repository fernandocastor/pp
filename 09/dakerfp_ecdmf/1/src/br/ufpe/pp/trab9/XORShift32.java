/*
 * XORShift32.java
 * Copyright (C) 2015 Emiliano Firmino <emiliano.firmino@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */

// Article: https://dmurphy747.wordpress.com/2011/03/23/xorshift-vs-random-performance-in-java/
// Based on: https://github.com/roquendm/JGO-Grabbag/blob/master/src/roquen/math/rng/XorShift32.java

package br.ufpe.pp.trab9;

public class XORShift32
{
    private int value;

    public XORShift32(int seed) {
        value = seed != 0 ? seed : (int) (System.currentTimeMillis() / Integer.MAX_VALUE);

    }

    public int nextInt()
    {
        value ^= (value <<  13);
        value ^= (value >>> 17);
        value ^= (value <<  15);

        return value;
    }
}


