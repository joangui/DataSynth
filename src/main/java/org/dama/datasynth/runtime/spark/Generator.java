package org.dama.datasynth.runtime.spark;

/**
 * Created by aprat on 13/09/16.
 */
public class Generator extends ExpressionValue {

    private org.dama.datasynth.generators.Generator generator;

    public Generator(org.dama.datasynth.generators.Generator generator) {
        this.generator = generator;
    }

    public org.dama.datasynth.generators.Generator getGenerator() {
        return generator;
    }
}
