package org.dama.datasynth.exec;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;

import java.util.*;
import java.io.Serializable;
/**
 * Created by aprat on 10/04/16.
 */
public class ExecutionPlan implements Serializable{

    private  List<Task> entryPoints = new ArrayList<Task>();

    /**
     * Class Constructor
     */
    public ExecutionPlan() {

    }

    public List<Task> getEntryPoints() {
        return entryPoints;
    }

    /**
     * Initializes the execution plan from the given ast
     * @param ast The ast to initialize the execution plan from
     */
    public void initialize(Ast ast) throws BuildExecutionPlanException {

        Set<Task> processed = new TreeSet<Task>((t1,t2) -> { return t1.getOutput().compareTo(t2.getOutput());});
        Map<String,Task> tasks = new TreeMap<String,Task>();
        for(Ast.Entity entity : ast.getEntities()) {
            for(Ast.Attribute attribute : entity.getAttributes()) {
                Task task = new Task(entity,attribute);
                tasks.put(task.getOutput(),task);
            }
        }

        for(Map.Entry<String,Task> task : tasks.entrySet() ) {
            if( !processed.contains(task.getValue())) {
                List<Task> toProcess = new LinkedList<Task>();
                toProcess.add(task.getValue());
                while(!toProcess.isEmpty()) {
                    Task currentTask = toProcess.get(0);
                    toProcess.remove(0);
                    processed.add(currentTask);
                    for (String param : currentTask.getRunParameters()) {
                        Task otherTask = tasks.get(Task.taskName(currentTask.getEntity(), param));
                        if (otherTask != null) {
                            if (!processed.contains(otherTask)) {
                                toProcess.add(otherTask);
                            }
                            task.getValue().addDependant(otherTask);
                            otherTask.addDependee(task.getValue());
                        }
                    }
                }
            }
        }
        if(processed.size() != tasks.size()) throw new BuildExecutionPlanException("Execution plan wrongly built. Missing tasks");
        for(Map.Entry<String,Task> task : tasks.entrySet() ) {
            if(task.getValue().getDependants().size() == 0) {
                entryPoints.add(task.getValue());
            }
        }
    }
}
