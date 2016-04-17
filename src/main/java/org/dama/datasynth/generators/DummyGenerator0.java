package org.dama.datasynth.generators;

import org.dama.datasynth.runtime.Generator;

/**
 * Created by aprat on 17/04/16.
 */
public class DummyGenerator0 extends Generator {

    String toWrite;
    public void initialize(String toWrite) {
        this.toWrite = toWrite;
    }

    public void release() {

    }

    public String run() {
        return toWrite;
    }
}
