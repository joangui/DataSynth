package org.dama.datasynth.runtime.spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.utils.Tuple;

/**
 * Created by aprat on 13/09/16.
 */
public class Table extends ExpressionValue {

    private JavaPairRDD<Long,Tuple> rdd = null;
    private int cardinality = 0;

    public Table(JavaPairRDD<Long,Tuple> rdd, int cardinality) {
        this.rdd = rdd;
        this.cardinality = cardinality;
    }

    public JavaPairRDD<Long, Tuple> getRdd() {
        return rdd;
    }

    public int getCardinality() {
        return cardinality;
    }

}
