package org.dama.datasynth.utils;

import java.util.Random;

/**
 * Created by quim on 4/18/16.
 * Cummulative Distribution Function sampler
 */
public class UniformDistributionSampler extends Sampler{

    private String[] data = null;

    public UniformDistributionSampler(String[] data){
        this.data = data;
    }

    /**
     * Takes a sample of the colelction based on the Cummulative Distribution Function
     * @return
     */
    public String takeSample(long seed){
        Random random = new Random(seed);
        int position = random.nextInt(data.length);
        return data[position];
    }
}
