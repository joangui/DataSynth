package org.dama.datasynth.generators;

import org.dama.datasynth.runtime.Generator;

import java.util.Random;

/**
 * Created by aprat on 5/09/16.
 */
public class UniformDistribution extends Generator {

    private long min = 0;
    private long max = 1;
    private Random random = null;

    public void initialize(long min, long max ) {
        random = new Random();
    }

    public Long run(Long id) {
        return (long)random.nextInt((int)(max-min)) + min;
    }

}
