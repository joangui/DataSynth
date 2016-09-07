package org.dama.datasynth.lang.dependencygraph.builder;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.AstVisitor;
import org.dama.datasynth.lang.dependencygraph.*;

import java.util.List;

/**
 * Created by aprat on 4/09/16.
 */
public class CrossDependenciesInserter extends AstVisitor<Ast.Node> {

    private DependencyGraph graph = null;


    public CrossDependenciesInserter() {
    }


    public void run(DependencyGraph graph, Ast ast) {
        this.graph = graph;
        for(Ast.Entity entity : ast.getEntities().values()) {
            entity.accept(this);
        }

        for(Ast.Edge edge : ast.getEdges().values()) {
            edge.accept(this);
        }
    }

    private void solveGeneratorDependencieS(Vertex vertex, Ast.Generator astGenerator, String generatorRelationName) {

        List<Vertex> neighbors = graph.getNeighbors(vertex,generatorRelationName);
        Generator generator = (Generator)(neighbors.get(0));
        for(Ast.Atomic param : astGenerator.getRunParameters()) {
            Attribute attributeParameter = graph.getAttribute(param.getName());
            graph.addDependency(generator,attributeParameter,"runParameter");
        }

    }

    @Override
    public Ast.Entity visit(Ast.Entity astEntity) {
        for(Ast.Attribute astAttribute : astEntity.getAttributes().values()) {
            astAttribute.accept(this);
        }
        return astEntity;
    }

    @Override
    public Ast.Edge visit(Ast.Edge astEdge) {
        Edge edge = graph.getEdge(astEdge.getName());
        if(astEdge.getSourceCardinalityGenerator() != null) {
           solveGeneratorDependencieS(edge,astEdge.getSourceCardinalityGenerator(),"sourceCardinality");
        }

        if(astEdge.getTargetCardinalityGenerator() != null) {
            solveGeneratorDependencieS(edge,astEdge.getTargetCardinalityGenerator(),"targetCardinality");
        }
        return astEdge;
    }


    @Override
    public Ast.Attribute visit(Ast.Attribute astAttribute) {
        Attribute attribute = graph.getAttribute(astAttribute.getName());
        solveGeneratorDependencieS(attribute,astAttribute.getGenerator(),"generator");
        return astAttribute;
    }

}
