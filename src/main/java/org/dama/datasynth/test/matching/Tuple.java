package org.dama.datasynth.test.matching;

/**
 * Created by aprat on 5/03/17.
 */
public class Tuple<X extends Comparable<X>,Y extends Comparable<Y>> implements Comparable<Tuple<X,Y>>{
    private X xvalue;
    private Y yvalue;
    Tuple(X xalue, Y yvalue) {
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
}
