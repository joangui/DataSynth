package org.dama.datasynth.test.matching;

/**
 * Created by aprat on 5/03/17.
 */
public class Tuple<X extends Comparable<X>,Y extends Comparable<Y>> implements Comparable<Tuple<X,Y>>{
    private X xvalue = null;
    private Y yvalue = null;
    Tuple(X xvalue, Y yvalue) {
        this.xvalue = xvalue;
        this.yvalue = yvalue;
    }

    public X getXvalue() {
        return xvalue;
    }

    public void setXvalue(X xvalue) {
        this.xvalue = xvalue;
    }

    public Y getYvalue() {
        return yvalue;
    }

    public void setYvalue(Y yvalue) {
        this.yvalue = yvalue;
    }

    @Override
    public int compareTo(Tuple<X, Y> o) {
        int xcomparison = xvalue.compareTo(o.xvalue);
        if(xcomparison != 0) {
            return xcomparison;
        }
        return yvalue.compareTo(o.yvalue);
    }

    @Override
    public boolean equals(Object o) {
        if(this.getClass().getName().compareTo(o.getClass().getName()) != 0) return false;
        Tuple<X,Y> other = (Tuple<X,Y>)o;
        return compareTo(other) == 0;
    }
}
