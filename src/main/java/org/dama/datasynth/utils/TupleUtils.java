package org.dama.datasynth.utils;

/**
 * Created by quim on 5/1/16.
 */
public final class TupleUtils {
    public static Tuple concatenate(Tuple t1, Tuple t2){
        Tuple r = new Tuple(t1);
        r.addAll(t2);
        return r;
    }
}
