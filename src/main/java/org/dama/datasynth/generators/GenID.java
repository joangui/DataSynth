package org.dama.datasynth.generators;

import org.apache.spark.api.java.*;
import scala.Tuple2;
import java.util.Arrays;
import java.util.List;
import org.dama.datasynth.SparkEnv;

public class GenID {
    public GenID(){}
    public JavaRDD<Integer> generateIds(Integer n) {
        Integer[] values = new Integer[n];
        for(int i = 0; i < n; ++i){
            values[i] = i+1;
        }
        return SparkEnv.sc.parallelize(Arrays.asList(values));
    }
}
