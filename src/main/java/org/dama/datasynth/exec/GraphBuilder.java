package org.dama.datasynth.exec;

import org.dama.datasynth.lang.Ast;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

/**
 * Created by quim on 5/10/16.
 */
public class GraphBuilder {
    private DirectedGraph<Vertex, DEdge> g;
    private List<Task> entryPoints = new ArrayList<Task>();
    public GraphBuilder(Ast t){

    }
    public void initialize(Ast ast) throws BuildExecutionPlanException {
        this.g = new DefaultDirectedGraph<>((v1, v2) -> new DEdge(v1, v2));
        for(Ast.Entity entity : ast.getEntities()) {
            EntityVertex ev = new EntityVertex(entity.getName());
            g.addVertex(ev);
            for(Ast.Attribute attribute : entity.getAttributes()) {
                AttributeVertex av = new AttributeVertex(attribute.getName());
                g.addVertex(av);
                g.addEdge(av, ev);
            }
        }

        //############################################################################
        Map<String,AttributeTask> tasks = new TreeMap<String,AttributeTask>();
        for(Ast.Entity entity : ast.getEntities()) {
            Task entityTask = new EntityTask(entity.getName(),entity.getNumEntities());
            entryPoints.add(entityTask);
            for(Ast.Attribute attribute : entity.getAttributes()) {
                AttributeTask task = new AttributeTask(entity,attribute);
                tasks.put(task.getTaskName(),task);
            }
        }
        //############################################################################

        Set<AttributeTask> processed = new TreeSet<AttributeTask>((t1, t2) -> { return t1.getTaskName().compareTo(t2.getTaskName());});
        for(Map.Entry<String,AttributeTask> task : tasks.entrySet() ) {
            if( !processed.contains(task.getValue())) {
                List<AttributeTask> toProcess = new LinkedList<AttributeTask>();
                toProcess.add(task.getValue());
                while(!toProcess.isEmpty()) {
                    AttributeTask currentTask = toProcess.get(0);
                    toProcess.remove(0);
                    processed.add(currentTask);
                    for (String param : currentTask.getRunParameters()) {
                        AttributeTask otherTask = tasks.get(currentTask.getEntity()+"."+param);
                        if (otherTask != null) {
                            if (!processed.contains(otherTask)) {
                                toProcess.add(otherTask);
                            }
                            task.getValue().addDependee(otherTask);
                            otherTask.addDependant(task.getValue());
                        }
                    }
                }
            }
        }
        if(processed.size() != tasks.size()) throw new BuildExecutionPlanException("Execution plan wrongly built. Missing tasks");
        for(Map.Entry<String,AttributeTask> task : tasks.entrySet() ) {
            if(task.getValue().getDependees().size() == 0) {
                for(Task entryPoint : entryPoints) {
                    if(entryPoint.getTaskName().compareTo(task.getValue().getEntity()) == 0) {
                        entryPoint.addDependant(task.getValue());
                        task.getValue().addDependee(entryPoint);
                    }
                }
            }
        }
    }
}
