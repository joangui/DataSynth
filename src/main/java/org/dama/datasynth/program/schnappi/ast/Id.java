package org.dama.datasynth.program.schnappi.ast;

import org.dama.datasynth.program.schnappi.ast.visitor.Visitor;

/**
 * Created by quim on 5/18/16.
 */
public class Id extends Expression{


    private String name = null;

    public Id(String name){
        this.name = name;
    }

    public Id(Id id) {
        name = id.name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Id copy() {
        return new Id(this);
    }
}
