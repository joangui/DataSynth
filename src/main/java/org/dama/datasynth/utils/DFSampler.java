package org.dama.datasynth.utils;

import java.util.Random;

/**
 * Created by quim on 4/19/16.
 */
public class DFSampler extends Sampler{
        public DFSampler(String str){
            super(str);
        }
        public String takeSample(long seed){
            this.g.setSeed(seed);
            Double u = this.g.nextDouble();
            Double sum = 0.0;
            for(int i = 0; i < s.size(); ++i){
                sum += Double.parseDouble(s.array[i][1]);
                if(u <= sum) {
                    return s.array[i][0];
                }
            }
            return "NULL";
        }
}
