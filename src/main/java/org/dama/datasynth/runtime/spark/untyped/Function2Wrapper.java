package org.dama.datasynth.runtime.spark.untyped;

import org.apache.spark.api.java.function.Function;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.runtime.Generator;
import org.dama.datasynth.runtime.spark.MethodSerializable;
import org.dama.datasynth.utils.Tuple;

import java.util.List;


/**
 * Created by aprat on 17/04/16.
 */
public class Function2Wrapper extends UntypedMethod implements Function<Tuple, Tuple> {

    public Function2Wrapper(Generator g, String functionName) {
        super(g,functionName);
    }

    public Tuple call(Tuple t) {
            System.out.println("WOOT "+this.g.getClass().getName());
            return new Tuple(invoke(t.get(1), t.get(2)));
    }
}
