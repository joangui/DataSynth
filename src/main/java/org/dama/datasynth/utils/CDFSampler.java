package org.dama.datasynth.utils;

import java.util.Random;

/**
 * Created by quim on 4/18/16.
 */
public class CDFSampler extends Sampler{
    public CDFSampler(String str){
        super(str);
    }
    public String takeSample(long seed){
        this.g.setSeed(seed);
        Double u = this.g.nextDouble();
        for(int i = 0; i < s.size(); ++i){
            if(u <= Double.parseDouble(s.array[i][1])) {
                return s.array[i][0];
            }
        }
        return "NULL";
    }
}
