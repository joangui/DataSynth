package org.dama.datasynth.runtime.spark.untyped;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.dama.datasynth.runtime.Generator;
import org.dama.datasynth.utils.Tuple;
import scala.Tuple2;

import java.util.Iterator;

/**
 * Created by quim on 9/13/16.
 */
public class FuncPartWrapper extends UntypedMethod implements PairFlatMapFunction<Iterator<Tuple2<Long,Tuple>>,Long,Tuple> {
    public FuncPartWrapper(Generator g, String functionName){
        super(g,functionName);
    }
    public Iterable<Tuple2<Long, Tuple>> call(Iterator<scala.Tuple2<Long, Tuple>> tuples){
        return (Iterable<scala.Tuple2<Long, Tuple>>) invoke(tuples);
    }
}
