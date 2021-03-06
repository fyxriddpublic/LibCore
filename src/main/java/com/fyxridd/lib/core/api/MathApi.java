package com.fyxridd.lib.core.api;

import org.apache.commons.math3.random.RandomDataGenerator;

public class MathApi {
    private static final org.apache.commons.math3.random.RandomDataGenerator RandomDataGenerator = new RandomDataGenerator();

    /**
     * upper >= lower
     */
    public static int nextInt(int lower, int upper) {
        if (upper == lower) return upper;
        return RandomDataGenerator.nextInt(lower, upper);
    }

    /**
     * upper >= lower
     */
    public static long nextLong(long lower, long upper) {
        if (upper == lower) return upper;
        return RandomDataGenerator.nextLong(lower, upper);
    }
}
