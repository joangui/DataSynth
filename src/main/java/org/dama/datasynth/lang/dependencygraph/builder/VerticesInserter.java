package org.dama.datasynth.lang.dependencygraph.builder;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.AstVisitor;
import org.dama.datasynth.lang.dependencygraph.*;

/**
 * Created by aprat on 4/09/16.
 * Ast visitor that adds the corresponding vertices to a dependency graph.
 */
public class VerticesInserter extends AstVisitor<Vertex> {

    private DependencyGraph graph = null;

    /**
     * Constructor
     */
    public VerticesInserter() {
    }

    /**
     * Executes the visitor over the ast.
     * @param graph The dependency graph to build the dependencies at.
     * @param ast  The ast to get the dependencies from.
     */
    public void run(DependencyGraph graph, Ast ast) {
        this.graph = graph;
        for(Ast.Entity entity : ast.getEntities().values()) {
            entity.accept(this);
        }

        for(Ast.Edge edge : ast.getEdges().values()) {
            edge.accept(this);
        }
    }

    @Override
    public Vertex visit(Ast.Entity astEntity) {
        Entity entity = new Entity(astEntity.getName(), astEntity.getNumInstances());
        graph.addEntityVertex(entity);
        Attribute oidAttribute = new Attribute(entity.getName()+".oid", Types.DataType.LONG, true);
        graph.addAttributeVertex(oidAttribute);
        graph.addDependency(entity,oidAttribute,"oid");
        for(Ast.Attribute astAttribute : astEntity.getAttributes().values()) {
            astAttribute.accept(this);
            Attribute attribute = graph.getAttribute(astAttribute.getName());
            graph.addDependency(entity,attribute,"attribute");
        }
        return entity;
    }

    @Override
    public Vertex visit(Ast.Edge astEdge) {
        Edge edge = new Edge(astEdge.getName(), astEdge.getEdgeType());
        graph.addEdgeVertex(edge);
        if(astEdge.getSourceCardinalityGenerator() != null) {
            Attribute attribute = new Attribute(astEdge.getName()+".sourcecardinality", Types.DataType.LONG,true);
            graph.addAttributeVertex(attribute);
            graph.addDependency(attribute,visit(astEdge.getSourceCardinalityGenerator()),"generator");
            graph.addDependency(edge,attribute,"sourcecardinality");
        } else if(astEdge.getSourceCardinalityNumber() != null) {
            Literal number = new Literal(astEdge.getSourceCardinalityNumber());
            graph.addLiteralVertex(number);
            graph.addDependency(edge,number,"sourcecardinality");
        }

        if(astEdge.getTargetCardinalityGenerator() != null) {
            Attribute attribute = new Attribute(astEdge.getName()+".targetcardinality", Types.DataType.LONG, true);
            graph.addAttributeVertex(attribute);
            graph.addDependency(attribute,visit(astEdge.getTargetCardinalityGenerator()),"generator");
            graph.addDependency(edge,attribute,"targetcardinality");
        } else if(astEdge.getTargetCardinalityNumber() != null) {
            Literal number = new Literal(astEdge.getTargetCardinalityNumber());
            graph.addLiteralVertex(number);
            graph.addDependency(edge,number,"targetcardinality");
        }
        return edge;
    }

    @Override
    public Generator visit(Ast.Generator astGenerator) {
        Generator generator = new Generator(astGenerator.getName());
        graph.addGeneratorVertex(generator);
        for(Ast.Atomic atomic : astGenerator.getInitParameters()) {
            Literal literal = visit(atomic);
            graph.addDependency(generator,literal,"initparam");
        }
        return generator;
    }

    @Override
    public Literal visit(Ast.Atomic atomic) {
        Literal literal = new Literal(atomic.getElement());
        graph.addLiteralVertex(literal);
        return literal;
    }


    @Override
    public Attribute visit(Ast.Attribute astAttribute) {
        Attribute attribute = new Attribute(astAttribute.getName(),astAttribute.getType(),true);
        graph.addAttributeVertex(attribute);
        Ast.Generator astGenerator = astAttribute.getGenerator();
        Generator generator = visit(astGenerator);
        graph.addDependency(attribute, generator,"generator");
        return attribute;
    }

}
