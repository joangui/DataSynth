package org.dama.datasynth.runtime.spark;

import java.io.Serializable;

/**
 * Created by aprat on 13/09/16.
 */
public class Generator extends ExpressionValue implements Serializable {

    private org.dama.datasynth.generators.Generator generator;

    public Generator(org.dama.datasynth.generators.Generator generator) {
        this.generator = generator;
    }

    public org.dama.datasynth.generators.Generator getGenerator() {
        return generator;
    }
}
