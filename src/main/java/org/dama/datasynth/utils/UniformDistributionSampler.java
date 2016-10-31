package org.dama.datasynth.utils;

import java.util.Random;

/**
 * Created by quim on 4/18/16.
 * Cummulative Distribution Function sampler
 */
public class UniformDistributionSampler extends Sampler{

    private String[] data = null;
    private Random random = null;


    public UniformDistributionSampler(String[] data, Long seed ){
        this.data = data;
        this.random = new Random(seed);
    }

    /**
     * Takes a sample of the colelction based on the Cummulative Distribution Function
     * @return
     */
    public String takeSample(){
        int position = random.nextInt(data.length);
        return data[position];
    }
}
