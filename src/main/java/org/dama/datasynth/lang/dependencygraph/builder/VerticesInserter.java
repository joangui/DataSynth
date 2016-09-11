package org.dama.datasynth.lang.dependencygraph.builder;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.AstVisitor;
import org.dama.datasynth.lang.dependencygraph.*;

/**
 * Created by aprat on 4/09/16.
 */
public class VerticesInserter extends AstVisitor<Vertex> {

    private DependencyGraph graph = null;


    public VerticesInserter() {
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

    @Override
    public Vertex visit(Ast.Entity astEntity) {
        Entity entity = new Entity(astEntity.getName(), astEntity.getNumInstances());
        graph.addEntityVertex(entity);
        Attribute oidAttribute = new Attribute("person.oid", Types.DataType.LONG);
        graph.addAttributeVertex(oidAttribute);
        graph.addDependency(entity,oidAttribute,"attribute");
        for(Ast.Attribute astAttribute : astEntity.getAttributes().values()) {
            astAttribute.accept(this);
            Attribute attribute = graph.getAttribute(astAttribute.getName());
            graph.addDependency(entity,attribute,"attribute");
        }
        return entity;
    }

    @Override
    public Vertex visit(Ast.Edge astEdge) {
        Edge edge = new Edge(astEdge.getName(), astEdge.getDirection());
        graph.addEdgeVertex(edge);
        if(astEdge.getSourceCardinalityGenerator() != null) {
            graph.addDependency(edge,visit(astEdge.getSourceCardinalityGenerator()),"sourceCardinality");
        } else if(astEdge.getSourceCardinalityNumber() != null) {
            Literal number = new Literal(astEdge.getSourceCardinalityNumber().toString(), Types.DataType.LONG);
            graph.addLiteralVertex(number);
            graph.addDependency(edge,number,"sourceCardinality");
        }

        if(astEdge.getTargetCardinalityGenerator() != null) {
            graph.addDependency(edge,visit(astEdge.getTargetCardinalityGenerator()),"targetCardinality");
        } else if(astEdge.getTargetCardinalityNumber() != null) {
            Literal number = new Literal(astEdge.getTargetCardinalityNumber().toString(), Types.DataType.LONG);
            graph.addLiteralVertex(number);
            graph.addDependency(edge,number,"targetCardinality");
        }

        graph.addDependency(edge,visit(astEdge.getCorrellation()),"correllation");
        return edge;
    }

    @Override
    public Generator visit(Ast.Generator astGenerator) {
        Generator generator = new Generator(astGenerator.getName());
        graph.addGeneratorVertex(generator);
        for(Ast.Atomic atomic : astGenerator.getInitParameters()) {
            Literal literal = visit(atomic);
            graph.addDependency(generator,literal,"init");
        }
        return generator;
    }

    @Override
    public Literal visit(Ast.Atomic atomic) {
        Literal literal = new Literal(atomic.getName(), atomic.getDataType());
        graph.addLiteralVertex(literal);
        return literal;
    }


    @Override
    public Attribute visit(Ast.Attribute astAttribute) {
        Attribute attribute = new Attribute(astAttribute.getName(),astAttribute.getType());
        graph.addAttributeVertex(attribute);
        Ast.Generator astGenerator = astAttribute.getGenerator();
        Generator generator = visit(astGenerator);
        graph.addDependency(attribute, generator,"generator");
        return attribute;
    }

}
