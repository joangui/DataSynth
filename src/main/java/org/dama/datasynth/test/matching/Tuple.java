package org.dama.datasynth.test.matching;

/**
 * Created by aprat on 5/03/17.
 */
public class Tuple<X extends Comparable<X>,Y extends Comparable<Y>> implements Comparable<Tuple<X,Y>>{

    private X x = null;
    private Y y = null;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X getX() {
        return x;
    }

    public void setX(X x) {
        this.x = x;
    }

    public Y getY() {
        return y;
    }

    public void setY(Y y) {
        this.y = y;
    }

    @Override
    public int compareTo(Tuple<X, Y> o) {
        int xcomparison = x.compareTo(o.x);
        if(xcomparison != 0) {
            return xcomparison;
        }
        return y.compareTo(o.y);
    }

    @Override
    public boolean equals(Object o) {
        if(this.getClass().getName().compareTo(o.getClass().getName()) != 0) return false;
        Tuple<X,Y> other = (Tuple<X,Y>)o;
        return compareTo(other) == 0;
    }
}
