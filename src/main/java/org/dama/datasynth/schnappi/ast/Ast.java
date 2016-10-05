package org.dama.datasynth.schnappi.ast;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by quim on 5/5/16.
 * Represents an Schnappi Ast
 */
public class Ast {

    protected List<Operation> operations = new LinkedList<Operation>();

    /**
     * Constructor
     */
    public Ast() {
    }

    /**
     * Copy Constructor
     * @param ast The ast to copy from
     */
    public Ast(Ast ast) {
        for(Operation operation : ast.operations) {
            operations.add(operation.clone());
        }
    }

    /**
     * Adds a statement to the Ast.
     * @param statement The statement to add
     */
    public void addOperation(Operation statement) {
        operations.add(statement);
    }

    /**
     *
     * @return
     */
    public List<Operation> getOperations(){
        return operations;
    }

}
