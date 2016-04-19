package org.dama.datasynth.utils;

import java.io.Serializable;
import java.util.Random;

public class Sampler implements Serializable{
    public SamplingSpace s;
    public Sampler(String str){
        this.initialize(str);
    }
    public void initialize(String str){
        s = new SamplingSpace(str);
    }
    public String takeSample(){
        Random g = new Random();
        return s.array[g.nextInt(s.size())].toString();
    }
}


