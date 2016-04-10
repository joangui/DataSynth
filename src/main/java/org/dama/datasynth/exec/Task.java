package org.dama.datasynth.exec;

import org.dama.datasynth.lang.Ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprat on 10/04/16.
 */
public class Task {

    /**
     * The list of tasks this task depends on
     */
    private List<Task> dependants = new ArrayList<Task>();

    /**
     * The list of tasks that depend on this task
     */
    private List<Task> dependees =  new ArrayList<Task>();


    private Ast.Entity      entity = null;
    private Ast.Attribute   attribute = null;

    /**
     * Class constructor
     * @param entity The entity this task is generating something for
     * @param attribute The attribute this task is generating
     */
    public Task(Ast.Entity entity, Ast.Attribute attribute ) {
        this.entity = entity;
        this.attribute = attribute;
    }

    /**
     * Gets the entity this task is generating something for
     * @return The entity
     */
    public Ast.Entity getEntity() {
        return entity;
    }

    /**
     * Gets the attribute this task is generating something for
     * @return The attribute
     */
    public Ast.Attribute getAttribute() {
        return attribute;
    }

    /**
     * Get the tasks this task depends on
     * @return The tasks this task depends on
     */
    public List<Task> getDependants() {
        return dependants;
    }

    /**
     * Get the tasks that depend on this task
     * @return The tasks taht depend on this task
     */
    public List<Task> getDependees() {
        return dependees;
    }

    /**
     * Adds a dependant on this task
     * @param task The task this task is dependant on
     */
    public void addDependant(Task task ) { dependants.add(task); }

    /**
     * Adds a task depending on this task
     * @param task The task depending no this task
     */
    public void addDependee(Task task ) { dependees.add(task); }

    /**
     * Gets the name of the task
     * @return The name of the task
     */
    public String getTaskName() {
        return taskName(entity.getName(),attribute.getName());
    }

    public static String taskName(String entity, String attribute ) {
        return entity+"."+attribute;
    }

}
