package org.dama.datasynth.exec;

import org.dama.datasynth.lang.Ast;

import java.util.*;

/**
 * Created by aprat on 10/04/16.
 */
public class ExecutionPlan {

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

        Set<Task> processed = new TreeSet<Task>((t1,t2) -> { return t1.getTaskName().compareTo(t2.getTaskName());});
        Map<String,Task> tasks = new TreeMap<String,Task>();
        for(Ast.Entity entity : ast.getEntities()) {
            for(Ast.Attribute attribute : entity.getAttributes()) {
                Task task = new Task(entity,attribute);
                tasks.put(task.getTaskName(),task);
            }
        }

        for(Map.Entry<String,Task> task : tasks.entrySet() ) {
            if( !processed.contains(task.getValue())) {
                Ast.Entity entity = task.getValue().getEntity();
                Ast.Attribute attribute = task.getValue().getAttribute();
                List<Task> toProcess = new LinkedList<Task>();
                toProcess.add(task.getValue());
                while(!toProcess.isEmpty()) {
                    Task currentTask = toProcess.get(0);
                    toProcess.remove(0);
                    processed.add(currentTask);
                    for (String param : attribute.getGenerator().getRunParameters()) {
                        Task otherTask = tasks.get(Task.taskName(entity.getName(), param));
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
        if(processed.size() != tasks.size()) throw new BuildExecutionPlanException("Execution plant wrongly built. Missing tasks");
        for(Map.Entry<String,Task> task : tasks.entrySet() ) {
            if(task.getValue().getDependants().size() == 0) {
                entryPoints.add(task.getValue());
            }
        }
    }
}
