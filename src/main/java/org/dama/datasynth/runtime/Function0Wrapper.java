package org.dama.datasynth.runtime;

import org.apache.spark.api.java.function.Function;
import org.dama.datasynth.common.Types;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprat on 17/04/16.
 */
public class Function0Wrapper extends MethodSerializable implements Function<Long, Object> {


    public Function0Wrapper(Generator g, String functionName, List<Types.DATATYPE> parameters, Types.DATATYPE returnType) {
        super(g,functionName,parameters,returnType);
    }

    public Object call(Long l) {
            return (Object)invoke();
    }
}
