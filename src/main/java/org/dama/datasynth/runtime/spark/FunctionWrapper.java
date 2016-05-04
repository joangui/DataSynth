package org.dama.datasynth.runtime.spark;

import org.apache.spark.api.java.function.Function;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.runtime.Generator;
import org.dama.datasynth.utils.Tuple;

import java.util.List;


/**
 * Created by aprat on 17/04/16.
 */
public class FunctionWrapper extends MethodSerializable implements Function<Tuple,Tuple> {

    public FunctionWrapper(Generator g, String functionName, List<Types.DATATYPE> parameters, Types.DATATYPE returnType) {
        super(g,functionName,parameters, returnType);
    }

    public Tuple call(Tuple t) {
            return new Tuple(invoke(t.get(1)));
    }


}
