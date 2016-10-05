package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;

/**
 * Created by quim on 4/27/16.
 * Represents an edge element in the dependency graph.
 */
public class Edge extends Vertex {

    /**
     * Constructor
     * @param name The name of the edge
     * @param edgeType The edgeType of the edge
     */
    public Edge(String name, Types.EdgeType edgeType) {
        super();
        properties.put("name",new Types.Id(name,false));
        properties.put("edgeType", edgeType);
    }

    /**
     * Gets the name of the edge
     * @return The name of the edge
     */
    public String getName() {
        return properties.get("name").toString();
    }

    /**
     * Gets the direction of the edge
     * @return The direction of the edge.
     */
    public Types.EdgeType getEdgeType() {
        return (Types.EdgeType)(properties.get("edgeType"));
    }

    @Override
    public void accept(DependencyGraphVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString(){
        return "[" + getName() + ","+getEdgeType().toString()+","+getClass().getSimpleName()+"]";
    }
}
