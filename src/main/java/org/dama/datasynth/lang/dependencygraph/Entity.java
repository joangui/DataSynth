package org.dama.datasynth.lang.dependencygraph;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by aprat on 20/04/16.
 */
public class Entity extends Vertex  {

    /**
     * The entity name
     */
    private String  entity;

    /**
     * The name of the entity
     * @param entity
     */
    public Entity(DependencyGraph graph, String entity) {
        super(graph,entity);
        this.entity = entity;
    }

    @Schnappi(name="dependencies")
    public List<String> dependencyNames(){
        List<String> ret = new LinkedList<String>();
        for(DEdge edge : graph.outgoingEdgesOf(this)) {
            Vertex neighbor = edge.getTarget();
            if(neighbor.getType().compareTo("Attribute") == 0) {
                Attribute attribute = (Attribute)neighbor;
                ret.add(attribute.getEntity().getName()+"."+attribute.getAttributeName());
            }
        }
        return ret;
    }

    /**
     * Gets the entity name
     * @return The entity name
     */
    public String getEntity() {
        return entity;
    }

    @Override
    public void accept(DependencyGraphVisitor visitor) {
        visitor.visit(this);
    }
}
