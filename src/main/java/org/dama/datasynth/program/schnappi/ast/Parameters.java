package org.dama.datasynth.program.schnappi.ast;

import org.dama.datasynth.program.schnappi.ast.visitor.Visitor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by quim on 5/19/16.
 */
public class Parameters extends Statement {

    public List<Expression> params = new LinkedList<Expression>();

    public Parameters(Expression ... exprs){
        for(Expression expr : exprs) {
            params.add(expr);
        }
    }

    public Parameters(Parameters parameters) {
        for(Expression param : parameters.params) {
            this.params.add((Expression)param.copy());
        }
    }

    public void mergeParams(Parameters n){
        this.params.addAll(n.params);
    }

    public void addParam(Expression id){
        this.params.add(id);
    }

    public Expression getParam(int i){
        return this.params.get(i);
    }

    @Override
    public String toString(){
        String str = "P<";
        if(params.size() > 0){
            String aux = "";
            for(Expression s : params){
                aux += ", " + s.toString();
            }
            str += aux.substring(1);
        }else {
            str += "";
        }
        str += " >";
        return str;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public List<Expression> getParams() {
        return params;
    }

    @Override
    public Statement copy() {
        return new Parameters(this);
    }
}
