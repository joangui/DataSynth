package org.dama.datasynth.program.schnappi.ast;

import org.dama.datasynth.program.schnappi.ast.visitor.Visitor;

/**
 * Created by quim on 5/26/16.
 */
public class Binding extends Statement {
    private String lhs;
    private String rhs;
    public Binding(String lhs, String rhs){
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Binding copy(){
        Binding bn = new Binding(lhs,rhs);
        return bn;
    }
    public String toString() {
        return "<" + lhs + " = " + rhs + "," + getType() + ">";
    }

    public String getLhs() {
        return lhs;
    }

    public String getRhs() {
        return rhs;
    }
}
