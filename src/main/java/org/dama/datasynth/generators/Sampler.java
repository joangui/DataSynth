package org.dama.datasynth.generators;

import java.util.Random;
import org.apache.spark.api.java.*;
import scala.Tuple2;
import org.apache.spark.api.java.function;

public class Sampler{
    public TextFile tf;
    public Sampler(String file){
        tf = new TextFile(file);
    }
    public Tuple2<Integer, String> run(Integer x){
        Random g = new Random();
        return new Tuple2<Integer, String>(x,tf.array[g.nextInt(tf.array.length)]);
    }
}
