package org.dama.datasynth;

import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;

public final class SparkEnv{
    public static SparkConf conf;
    public static JavaSparkContext sc;
    public static void initialize(){
        conf = new SparkConf().setAppName("DataSynth").setMaster("local[4]");
        sc = new JavaSparkContext(conf);
    }
}
