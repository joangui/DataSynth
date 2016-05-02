package org.dama.datasynth.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 5/1/16.
 */
public class Tuple {
    private List<Object> elems;

    public Tuple() {
        this.elems = new ArrayList<>();
    }
    public Tuple(Tuple other){
        this.elems = new ArrayList<>(other.elems);
    }
    public Object get(int i){
        return this.elems.get(i);
    }
    public void set(int i, Object obj) {
        this.elems.set(i, obj);
    }
    public void add(Object obj){
        this.elems.add(obj);
    }
    public void addAll(Tuple t){
        this.elems.addAll(t.elems);
    }
    public int size(){ return this.elems.size();}
}
