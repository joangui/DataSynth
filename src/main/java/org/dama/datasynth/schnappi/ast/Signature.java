package org.dama.datasynth.schnappi.ast;

/**
 * Created by quim on 5/25/16.
 */
public class Signature extends Node implements Comparable<Signature> {

    private String source = null;
    private String target = null;

    public Signature(String source, String target){
        this.source = source;
        this.target = target;
    }

    public Signature(Signature signature) {
        this.source = signature.source;
        this.target = signature.target;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Node copy() {
        return new Signature(this);
    }

    @Override
    public int compareTo(Signature o) {
        return source.compareToIgnoreCase(o.source);
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }
}
