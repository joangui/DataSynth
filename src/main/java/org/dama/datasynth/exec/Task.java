package org.dama.datasynth.exec;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.runtime.ExecutionEngine;
import org.dama.datasynth.runtime.ExecutionException;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
/**
 * Created by aprat on 10/04/16.
 */
public abstract class Task implements Serializable{

    /**
     * The list of tasks that depend on this task
     */
    private List<Task> dependants = new ArrayList<Task>();

    /**
     * The list of tasks this task depends on
     */
    private List<Task> dependees =  new ArrayList<Task>();


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
    abstract public String getTaskName();

    /**
     * Accepts an engine to execute the task
     * @param engine The engine to execute the task
     * @throws ExecutionException
     */
    abstract public void accept(ExecutionEngine engine ) throws ExecutionException;

}
