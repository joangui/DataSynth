package org.dama.datasynth.generators;

import org.dama.datasynth.runtime.Generator;

import java.util.Random;

/**
 * Created by aprat on 5/09/16.
 * Generates a value within a given range, uniformly distributed.
 */
public class UniformDistribution extends Generator {

    private long min = 0;
    private long max = 1;
    private Random random = null;

    /**
     * Initializes the generator
     * @param min The minimum value
     * @param max The maximum value
     */
    public void initialize(long min, long max ) {
        random = new Random();
    }

    /**
     * Generates a uniformly distributed value within the range
     * @param id Unused
     * @return The value uniformly distributed.
     */
    public Long run(Long id) {
        return (long)random.nextInt((int)(max-min)) + min;
    }

}
