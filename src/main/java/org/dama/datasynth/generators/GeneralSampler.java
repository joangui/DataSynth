package org.dama.datasynth.generators;

import java.util.Random;

/**
 * Created by quim on 4/18/16.
 */
public class GeneralSampler {
    public TextFile tf;
    public GeneralSampler(){}
    public void initialize(String file){
        tf = new TextFile(file);
    }
    public String run(){
        Random g = new Random();
        Double u = g.nextDouble();
        Double sum = 0.0;
        for(int i = 0; i < tf.array.length; ++i){
            sum += Double.parseDouble(tf.array[i][1]);
            if(sum < u) return tf.array[i][0];
        }
        return "NULL";
    }
}
