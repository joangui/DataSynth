package org.dama.datasynth.runtime.spark;

import org.apache.spark.api.java.function.Function;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.runtime.Generator;
import scala.Tuple2;
import scala.Tuple3;

import java.util.List;


/**
 * Created by aprat on 17/04/16.
 */
public class Function2Wrapper extends MethodSerializable implements Function<Tuple2<Tuple2<Object, Object>, Object>,Object> {

    public Function2Wrapper(Generator g, String functionName, List<Types.DATATYPE> parameters, Types.DATATYPE returnType) {
        super(g,functionName,parameters, returnType);
    }

    public Object call(Tuple2<Tuple2<Object,Object>,Object> t) {
            return (Object) invoke(t._1()._2(),t._2());
    }

    public Object call(Tuple2<Tuple2<Object,Object>,Object> t) {
        return (Object) invoke(t._1()._2(),t._2());
    }

}
