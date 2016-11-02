package org.dama.datasynth.generators;

import org.dama.datasynth.utils.MurmurHash;

import java.util.Random;

/**
 * Created by aprat on 5/09/16.
 * Generates a value within a given range, uniformly distributed.
 */
public class UniformDistribution extends Generator {

    private long min = 0;
    private long max = 1;

    /**
     * Initializes the generator
     * @param min The minimum value
     * @param max The maximum value
     */
    public void initialize(Long min, Long max ) {
        this.min = min;
        this.max = max;
    }

    /**
     * Generates a uniformly distributed value within the range
     * @return The value uniformly distributed.
     */
    public Long run(Long id) {
        Random random = new Random(MurmurHash.hash64(id.toString()));
        return (long)random.nextInt((int)(max-min)) + min;
    }

}
