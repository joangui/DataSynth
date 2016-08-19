package org.dama.datasynth.exec;

import org.dama.datasynth.runtime.ExecutionEngine;
import org.dama.datasynth.runtime.ExecutionException;
import org.dama.datasynth.utils.traversals.Visitor;

/**
 * Created by aprat on 20/04/16.
 */
public class EntityTask extends Vertex  {

    /**
     * The entity name
     */
    private String  entity;

    /**
     * The name of the entity
     * @param entity
     */
    public EntityTask(DependencyGraph graph, String entity) {
        super(graph, entity,"entity");
        this.entity = entity;
    }

    /**
     * Gets the entity name
     * @return The entity name
     */
    public String getEntity() {
        return entity;
    }

    @Override
    public void accept(Visitor visitor) {
        DependencyGraphVisitor v = (DependencyGraphVisitor)  visitor;
        v.visit(this);
    }

}
