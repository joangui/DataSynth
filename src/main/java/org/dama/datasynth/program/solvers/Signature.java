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

    public Boolean compareTo(Signature s){
        return (this.source.compareTo(s.getSource()) == 0) && (this.target.compareTo(s.getTarget()) == 0);
    }
}
