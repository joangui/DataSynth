package org.dama.datasynth.runtime;

import org.apache.spark.api.java.function.Function;
import org.dama.datasynth.common.Types;
import scala.Tuple2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by aprat on 17/04/16.
 */
public class FunctionWrapper extends MethodSerializable implements Function<Tuple2<Long, Object>,Object> {


    public FunctionWrapper(Generator g, String functionName, List<Types.DATATYPE> parameters, Types.DATATYPE returnType) {
        super(g,functionName,parameters, returnType);
    }

    public Object call(Tuple2<Long,Object> t) {
            return (Object) invoke(t._2());
    }


}
