package org.dama.datasynth.runtime.spark.untyped;

import org.apache.spark.api.java.function.Function;
import org.dama.datasynth.generators.Generator;
import org.dama.datasynth.utils.Tuple;


/**
 * Created by aprat on 17/04/16.
 */
public class FunctionWrapper extends UntypedMethod implements Function<Tuple,Tuple> {

    public FunctionWrapper(Generator g, String functionName) {
        super(g,functionName);
    }

    public Tuple call(Tuple t) {
            return new Tuple(invoke(t.get(0), t.get(1)));
    }
}