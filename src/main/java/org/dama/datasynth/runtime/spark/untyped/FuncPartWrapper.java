package org.dama.datasynth.runtime.spark.untyped;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.dama.datasynth.runtime.Generator;
import org.dama.datasynth.utils.Tuple;
import scala.Tuple2;

/**
 * Created by quim on 9/13/16.
 */
public class FuncPartWrapper extends UntypedMethod implements PairFlatMapFunction<Iterable<Tuple2<Long,Tuple>>,Long,Tuple> {
    //Function<Iterable<Tuple2<Long, Tuple>>,Iterable<Tuple2<Long, Tuple>>> {
    public FuncPartWrapper(Generator g, String functionName){
        super(g,functionName);
    }
    public Iterable<scala.Tuple2<Long, Tuple>> call(Iterable<scala.Tuple2<Long, Tuple>> tuples){
        return (Iterable<scala.Tuple2<Long, Tuple>>) invoke(tuples);
    }
}
