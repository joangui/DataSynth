package org.dama.datasynth.utils;

import java.util.Random;

/**
 * Created by quim on 4/18/16.
 * Cummulative Distribution Function sampler
 */
public class CumulativeDistributionSampler extends Sampler{

    private String[] data = null;
    private Double[] probs = null;

    public CumulativeDistributionSampler(String[] data, Double[] probs){
        this.data = data;
        this.probs = probs;
    }

    /**
     * Takes a sample of the colelction based on the Cummulative Distribution Function
     * @return
     */
    public String takeSample(long seed){
        Random random = new Random(seed);
        double u = random.nextDouble();
        int position = java.util.Arrays.binarySearch(probs, u);
        if(position < 0) position = Math.abs(position+1);
        if(position >= data.length) position = data.length-1;
        return data[position];
    }
}
