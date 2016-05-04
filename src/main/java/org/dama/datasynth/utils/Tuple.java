package org.dama.datasynth.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 5/1/16.
 */
public class Tuple implements Serializable {
    private List<Object> elems;

    public Tuple(Object... objs){
        this.elems = new ArrayList<>();
        for(Object obj: objs){
            this.elems.add(obj);
        }
    }
    public Tuple() {
        this.elems = new ArrayList<>();
    }
    public Tuple(Tuple other){
        this.elems = new ArrayList<>(other.elems);
    }
    public Tuple(List<Object> l){
        this.elems = new ArrayList<>(l);
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

    @Override
    public String toString(){
        if(this.size() == 1) {
            return this.get(0).toString();
        }else {
            String str = "(";
            str += this.get(0);
            for (Object obj : this.elems) {
                str += ", " + obj.toString();
            }
            str += ")";
            return str;
        }
    }
    public Tuple drop(int n){
        if(n >= this.size()) return new Tuple();
        else {
            return new Tuple(this.elems.subList(n+1, this.size()));
        }
    }
}
