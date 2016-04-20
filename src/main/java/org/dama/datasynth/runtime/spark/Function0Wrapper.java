package org.dama.datasynth.runtime.spark;

import org.apache.spark.api.java.function.Function;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.runtime.Generator;

import java.util.List;

/**
 * Created by aprat on 17/04/16.
 */
public class Function0Wrapper extends MethodSerializable implements Function<Object, Object> {


    public Function0Wrapper(Generator g, String functionName, List<Types.DATATYPE> parameters, Types.DATATYPE returnType) {
        super(g,functionName,parameters,returnType);
    }

    public Object call(Object l) {
            return (Object)invoke();
    }
}
