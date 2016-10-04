package org.dama.datasynth.utils;

import java.io.Serializable;
import java.util.Random;

public class Sampler implements Serializable{
    public SamplingSpace s;
    protected Random g;
    public Sampler(String str){
        this.initialize(str);
    }
    public void initialize(String str){
        s = new SamplingSpace(str);
        g = new Random();
    }
    public String takeSample(){
        return s.array[g.nextInt(s.size())][0].toString();
    }
}


