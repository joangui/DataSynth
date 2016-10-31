package org.dama.datasynth.utils;

import java.util.Random;

/**
 * Created by quim on 4/18/16.
 * Cummulative Distribution Function sampler
 */
public class CumulativeDistributionSampler extends Sampler{

    private String[] data = null;
    private Double[] probs = null;
    private Random random = null;

    public CumulativeDistributionSampler(String[] data, Double[] probs, Long seed ){
        this.data = data;
        this.probs = probs;
        this.random = new Random();
        this.random.setSeed(seed);
    }

    /**
     * Takes a sample of the colelction based on the Cummulative Distribution Function
     * @return
     */
    public String takeSample(){
        double u = random.nextDouble();
        int position = java.util.Arrays.binarySearch(probs, u);
        if(position < 1) position = Math.abs(position);
        if(position >= data.length) position = data.length-1;
        return data[position];
    }
}
