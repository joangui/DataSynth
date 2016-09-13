package org.dama.datasynth.runtime.spark;

/**
 * Created by aprat on 13/09/16.
 */
public class Literal extends ExpressionValue{

    private org.dama.datasynth.schnappi.ast.Literal literal;

    public Literal (org.dama.datasynth.schnappi.ast.Literal literal) {
        this.literal = literal;
    }

    public org.dama.datasynth.schnappi.ast.Literal getLiteral() {
        return literal;
    }
}
