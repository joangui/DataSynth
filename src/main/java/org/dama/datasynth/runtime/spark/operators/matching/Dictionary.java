package org.dama.datasynth.runtime.spark.operators.matching;

import java.util.HashMap;

/**
 * Created by aprat on 6/03/17.
 */
public class Dictionary<T extends Comparable<T>, S extends Comparable<S>> {

    private HashMap<T,S> data = new HashMap<>();

    public Dictionary(Table<T,S> table) {
        for( Tuple<T,S> entry : table) {
            data.put(entry.getX(),entry.getY());
        }
    }

    public S get(T key) {
        return data.get(key);
    }
}
