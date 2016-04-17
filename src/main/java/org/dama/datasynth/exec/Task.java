package org.dama.datasynth.exec;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
/**
 * Created by aprat on 10/04/16.
 */
public class Task implements Serializable{

    /**
     * The list of tasks this task depends on
     */
    private List<Task> dependants = new ArrayList<Task>();

    /**
     * The list of tasks that depend on this task
     */
    private List<Task> dependees =  new ArrayList<Task>();


    private String entity = null;
    private String attributeName = null;
    private Types.DATATYPE attributeType = null;

    public String getGenerator() {
        return generator;
    }

    private String generator = null;
    private List<String> runParameters = new ArrayList<String>();

    public List<String> getInitParameters() {
        return initParameters;
    }

    private List<String> initParameters = new ArrayList<String>();
    private String output;

    /**
     * Class constructor
     * @param entity The entity this task is generating something for
     * @param attribute The attribute this task is generating
     */
    public Task(Ast.Entity entity, Ast.Attribute attribute ) {
        this.entity = entity.getName();
        this.attributeName = attribute.getName();
        this.attributeType = attribute.getType();
        this.output = Task.taskName(entity.getName(),attribute.getName());
        this.generator = attribute.getGenerator().getName();
        for( String param : attribute.getGenerator().getRunParameters()) {
            this.runParameters.add(param);
        }

        for( String param : attribute.getGenerator().getInitParameters()) {
            this.initParameters.add(param);
        }
    }

    /**
     * Gets the entity this task is generating something for
     * @return The entity
     */
    public String getEntity() {
        return entity;
    }

    /**
     * Gets the name of the attribute this task is generating something for
     * @return The attribute
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Gets the type of the attribute this task is generating something for
     * @return The attribute
     */
    public Types.DATATYPE getAttributeType() {
        return attributeType;
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
     * Gets the output of the task
     * @return The output of the task
     */
    public String getOutput() {
        return output;
    }

    /**
     * Gets the parameters of the run method of the generator
     * @return The run parameters of the generator
     */
    public List<String> getRunParameters() {
        return runParameters;
    }

    public static String taskName(String entity, String attribute ) {
        return entity+"."+attribute;
    }

}
