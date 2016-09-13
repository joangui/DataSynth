package org.dama.datasynth.runtime.spark;

import org.apache.spark.api.java.function.Function;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.runtime.Generator;
import org.dama.datasynth.utils.Tuple;

import java.util.List;

/**
 * Created by aprat on 17/04/16.
 */
public class Function0Wrapper extends MethodSerializable implements Function<Tuple, Tuple> {

    public Function0Wrapper(Generator g, String functionName, List<Types.DataType> parameters, Types.DataType returnType) {
        super(g,functionName,parameters,returnType);
    }

    public Tuple call(Tuple l) {
            return new Tuple(invoke());
    }
}
