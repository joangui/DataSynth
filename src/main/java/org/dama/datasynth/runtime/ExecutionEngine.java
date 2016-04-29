package org.dama.datasynth.runtime;

import org.dama.datasynth.exec.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by aprat on 20/04/16.
 */
public abstract class ExecutionEngine {
    /**
     * Executes the execution plan
     * @param plan The execution plan to execute
     * @throws ExecutionException
     */
    public void execute(ExecutionPlan plan) throws ExecutionException {
        List<Task> todo = new LinkedList<Task>();
        todo.addAll(plan.getEntryPoints());
        while(!todo.isEmpty()) {
            Task task = todo.get(0);
            todo.remove(0);
            task.accept(this);
            for(Task next : task.getDependants()) {
                todo.add(next);
            }
        }
    }

    /**
     * Dumps the generated data to the output dir
     * @param outputdir The output dir
     */
    public abstract void dumpData(String outputdir);


    /**
     * Executes an attribute task
     * @param task The task to execute
     * @throws ExecutionException
     */
    public abstract void execute ( AttributeTask task ) throws ExecutionException;

    /**
     * Executes an entity task
     * @param task The task to execute
     * @throws ExecutionException
     */
    public abstract void execute ( EntityTask task ) throws ExecutionException;

    /**
     * Executes an edge task
     * @param task The task to execute
     * @throws ExecutionException
     */
    public abstract void execute (EdgeTask task ) throws ExecutionException;
}
