package org.dama.datasynth.generators;

import java.util.Random;
import org.apache.spark.api.java.*;
import scala.Tuple2;
import org.apache.spark.api.java.function.*;
import java.io.Serializable;
import org.dama.datasynth.runtime.Generator;

public class Sampler extends Generator{
    public TextFile tf;
    public Sampler(){}
    public void initialize(String file){
        tf = new TextFile(file);
    }
    public String run(Integer x){
        Random g = new Random();
        return new String(tf.array[g.nextInt(tf.array.length)]);
    }
}
