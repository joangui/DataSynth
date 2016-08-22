package org.dama.datasynth.program;

import org.dama.datasynth.program.schnappi.ast.Operation;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by quim on 5/5/16.
 */
public class Ast {

    protected List<Operation> statements = new LinkedList<Operation>();

    public Ast() {
    }

    public Ast(Ast ast) {
        for(Operation operation : ast.statements) {
            statements.add(operation.copy());
        }
    }

    public void addStatement(Operation statement) {
        statements.add(statement);
    }

    public List<Operation> getStatements(){
        return statements;
    }

    public void merge(Ast ast) {
        statements.addAll(ast.getStatements());
    }
}
