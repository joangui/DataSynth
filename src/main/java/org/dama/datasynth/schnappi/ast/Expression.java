package org.dama.datasynth.schnappi.ast;

/**
 * Created by quim on 5/18/16.
 */
public abstract class Expression extends Operation {

    @Override
    public abstract Expression copy();
}
