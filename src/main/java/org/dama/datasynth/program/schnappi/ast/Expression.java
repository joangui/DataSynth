package org.dama.datasynth.program.schnappi.ast;

/**
 * Created by quim on 5/18/16.
 */
public abstract class Expression extends Operation {
    @Override
    public abstract Expression copy();
}
