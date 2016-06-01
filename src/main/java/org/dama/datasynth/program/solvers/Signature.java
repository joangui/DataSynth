package org.dama.datasynth.program.solvers;

/**
 * Created by quim on 5/12/16.
 */
public class Signature {
    private String source;
    private String target;
    public Signature(String s, String t){
        this.source = s;
        this.target = t;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean compareTo(Signature s){
        return (this.source.compareTo(s.getSource()) == 0) && (this.target.compareTo(s.getTarget()) == 0);
    }
    @Override
    public boolean equals(Object obj){
        Signature s = (Signature) obj;
        return this.compareTo(s);
    }
    @Override
    public String toString(){
        return source + " => " + target;
    }
    @Override
    public int hashCode(){
        int p = 19;
        String str = source+target;
        return p*(str.hashCode());
    }
}
