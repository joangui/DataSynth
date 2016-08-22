package org.dama.datasynth.exec;

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
    public Entity(String entity) {
        super(entity);
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
