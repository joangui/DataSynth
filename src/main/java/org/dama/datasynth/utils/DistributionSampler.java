package org.dama.datasynth.utils;

/**
 * Created by quim on 4/19/16.
 * Distribution Function Sampler
 */
public class DistributionSampler extends Sampler{

    private CumulativeDistributionSampler sampler;

    public DistributionSampler(String[] data, Double[] probs){
        Double[] newProbs = new Double[probs.length];
        newProbs[0] = probs[0];
        for(int i = 1; i < probs.length; ++i) {
            newProbs[i] = probs[i] + newProbs[i-1];
        }
        this.sampler = new CumulativeDistributionSampler(data,newProbs);
    }

    @Override
    public String takeSample( long seed ) {
        return sampler.takeSample(seed);
    }
}
