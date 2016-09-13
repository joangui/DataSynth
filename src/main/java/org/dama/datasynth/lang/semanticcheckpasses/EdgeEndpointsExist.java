package org.dama.datasynth.lang.semanticcheckpasses;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.AstVisitor;
import org.dama.datasynth.lang.SemanticException;

/**
 * Created by aprat on 6/09/16.
 * Visitor that performs a semantic check over the ast.
 * Checks that the connecting endpoints of the edge exist.
 */
public class EdgeEndpointsExist extends AstVisitor<Ast.Node> {

    private Ast ast = null;

    /**
     * Performs the check over the ast.
     * @param ast The ast to check
     */
    public void check(Ast ast) {
        this.ast = ast;
        for(Ast.Edge edge : ast.getEdges().values()) {
            edge.accept(this);
        }
    }

    @Override
    public Ast.Node visit(Ast.Edge edge) {
        if(ast.getEntities().get(edge.getSource()) == null) {
            throw new SemanticException(SemanticException.SemanticExceptionType.EDGE_ENDPOINT_NOT_EXISTS, edge.getSource());
        }

        if(ast.getEntities().get(edge.getTarget()) == null) {
            throw new SemanticException(SemanticException.SemanticExceptionType.EDGE_ENDPOINT_NOT_EXISTS, edge.getTarget());
        }
        return edge;
    }
}
