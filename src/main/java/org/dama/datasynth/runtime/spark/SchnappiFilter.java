package org.dama.datasynth.runtime.spark;

import org.dama.datasynth.utils.Tuple;

import java.util.List;

/**
 * Created by quim on 9/5/16.
 */
public class SchnappiFilter {
    private List<Integer> indices;
    public SchnappiFilter(List<Integer> _indices){
        this.indices = _indices;
    }
    public Tuple call(Tuple t){
        Tuple result = new Tuple();
        for(Integer i : indices){
            result.add(t.get(i));
        }
        return result;
    }
}
