package org.dama.datasynth.utils;

import java.util.Random;

/**
 * Created by quim on 4/18/16.
 * Cummulative Distribution Function sampler
 */
public class CDFSampler extends Sampler{

    long seed = 0;

    /**
     * Constructor
     * @param str A string containing a sequence of probabilities pairs value-probability
     */
    public CDFSampler( String str, Long seed ){
        super(str);
        this.g.setSeed(seed);
        this.seed = seed;
    }

    /**
     * Takes a sample of the colelction based on the Cummulative Distribution Function
     * @return
     */
    public String takeSample(){
        Double u = this.g.nextDouble();
        for(int i = 0; i < s.size(); ++i){
            if(u <= Double.parseDouble(s.array[i][1])) {
                return s.array[i][0];
            }
        }
        return "NULL";
    }
}
