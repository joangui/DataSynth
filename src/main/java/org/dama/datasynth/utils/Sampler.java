package org.dama.datasynth.utils;

import java.util.Random;
import org.dama.datasynth.runtime.Generator;

public class Sampler {
    public SamplingSpace s;
    public Sampler(){}
    public void initialize(String str){
        s = new SamplingSpace(str);
    }
    public String takeSample(){
        Random g = new Random();
        return s.array[g.nextInt(s.size())].toString();
    }
}


