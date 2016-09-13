package org.dama.datasynth.utils;

import org.apache.spark.api.java.function.Function2;

/**
 * Created by quim on 5/1/16.
 */
public final class TupleUtils {
    public static Tuple concatenate(Tuple t1, Tuple t2){
        Tuple r = new Tuple(t1);
        r.addAll(t2);
        return r;
    }
    public static Function2<Tuple,Tuple,Tuple> join = new Function2<Tuple,Tuple,Tuple>(){
        @Override
        public Tuple call(Tuple t1, Tuple t2) {
            return TupleUtils.concatenate(t1,t2);
        }
    };
}
