package org.dama.datasynth.lang.dependencygraph.passes;

import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.AstVisitor;
import org.dama.datasynth.lang.dependencygraph.*;

import java.util.List;

/**
 * Created by aprat on 4/09/16.
 */
public class VerticesInserter implements AstVisitor {

    private DependencyGraph graph = null;


    public VerticesInserter(DependencyGraph graph) {
        this.graph = graph;
    }

    @Override
    public void visit(Ast.Entity astEntity) {
        Entity entity = new Entity(astEntity.getName(), astEntity.getNumInstances());
        graph.addEntityVertex(entity);
        for(Ast.Attribute astAttribute : astEntity.getAttributes()) {
            astAttribute.accept(this);
            Attribute attribute = graph.getAttribute(astAttribute.getName());
            graph.addDependency(entity,attribute,"attribute");
        }
    }

    @Override
    public void visit(Ast.Edge astEdge) {
        Edge edge = new Edge(astEdge.getName(), astEdge.getDirection());
        graph.addEdgeVertex(edge);
    }

    @Override
    public void visit(Ast.Generator generator) {
        throw new DependencyGraphConstructionException("Error when building dependency graph. Visit Generator on Vertices Inserter is not implemented");
    }

    @Override
    public void visit(Ast.Attribute astAttribute) {
        Attribute attribute = new Attribute(astAttribute.getName(),astAttribute.getType());
        graph.addAttributeVertex(attribute);
        Ast.Generator astGenerator = astAttribute.getGenerator();
        Generator generator = new Generator(astGenerator.getName());
        graph.addDependency(attribute,generator,"generator");
        throw new DependencyGraphConstructionException("Error when building dependency graph. Visit Attribute on Vertices Inserter is not implemented");

    }

    @Override
    public void visit(Ast.Atomic atomic) {
        throw new DependencyGraphConstructionException("Error when building dependency graph. Visit Atomic on Vertices Inserter is not implemented");
    }


}
