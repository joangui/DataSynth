package org.dama.datasynth.utils;

import java.util.Random;

/**
 * Created by quim on 4/19/16.
 */
public class DFSampler extends Sampler{
        public DFSampler(){}
        public String takeSample(){
            Random g = new Random();
            Double u = g.nextDouble();
            Double sum = 0.0;
            for(int i = 0; i < s.size(); ++i){
                sum += Double.parseDouble(s.array[i][1]);
                if(u <= sum) return s.array[i][0];
            }
            return "NULL";
        }
}
