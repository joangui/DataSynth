package org.dama.datasynth.lang.semanticcheckpasses;

import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.AstVisitor;
import org.dama.datasynth.lang.SemanticException;

/**
 * Created by aprat on 6/09/16.
 */
public class EdgeCorrelatesValid extends AstVisitor<Ast.Node> {

    private Ast ast = null;

    public void check(Ast ast) {
        this.ast = ast;
        for(Ast.Edge edge : ast.getEdges().values()) {
            edge.accept(this);
        }
    }

    @Override
    public Ast.Node visit(Ast.Edge edge) {
        for(Ast.Atomic atomic : edge.getCorrelates()) {
            String entityName = atomic.getName().substring(0,atomic.getName().indexOf("."));
            if(ast.getEntities().get(entityName) == null)  throw new SemanticException(SemanticException.SemanticExceptionType.ATTRIBUTE_NAME_UNEXISTING, atomic.getName());
        }
        return edge;
    }
}
