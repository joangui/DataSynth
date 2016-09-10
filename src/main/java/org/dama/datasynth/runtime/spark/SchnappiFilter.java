package org.dama.datasynth.runtime.spark;

import org.dama.datasynth.utils.Tuple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 9/5/16.
 */
public class SchnappiFilter implements Serializable {
    private List<Integer> indices;
    public SchnappiFilter(){
        this.indices = new ArrayList<Integer>();
    }
    public void initialize(Integer... ints){
        for(Integer intger : ints){
            this.indices.add(intger);
        }
    }
    public SchnappiFilter(List<Integer> _indices){
        this.indices = _indices;
    }
    public void addIndex(Integer ind){
        this.indices.add(ind);
    }
    public Tuple run(Tuple t){
        Tuple result = new Tuple();
        for(Integer i : indices){
            result.add(t.get(i));
        }
        return result;
    }
}
