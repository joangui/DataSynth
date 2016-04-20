package org.dama.datasynth.exec;

import org.dama.datasynth.runtime.ExecutionEngine;
import org.dama.datasynth.runtime.ExecutionException;

/**
 * Created by aprat on 20/04/16.
 */
public class EntityTask extends Task  {

    /**
     * The entity name
     */
    private String  entity;

    /**
     * The number of entities
     */
    private Long    number;

    public EntityTask(String entity, Long number) {
        this.entity = entity;
        this.number = number;
    }

    /**
     * Gets the entity name
     * @return The entity name
     */
    public String getEntity() {
        return entity;
    }

    /**
     * Gets the number of entities
     * @return The number of entities
     */
    public Long getNumber() {
        return number;
    }

    @Override
    public String getTaskName() {
        return entity;
    }

    @Override
    public void accept(ExecutionEngine engine ) throws ExecutionException {
        engine.execute(this);
    }
}
