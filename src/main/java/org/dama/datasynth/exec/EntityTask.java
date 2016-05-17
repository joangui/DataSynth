package org.dama.datasynth.exec;

import org.dama.datasynth.runtime.ExecutionEngine;
import org.dama.datasynth.runtime.ExecutionException;

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
    public EntityTask(String entity) {
        super(entity,"entity");
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
    public void accept(DependencyGraphVisitor visitor) {
        visitor.visit(this);
    }
}
