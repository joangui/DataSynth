package org.dama.datasynth.exec;

import org.dama.datasynth.DataSynth;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by quim on 5/10/16.
 */
public class GraphBuilder {

    private static final Logger logger= Logger.getLogger( DataSynth.class.getSimpleName() );

    private DirectedGraph<Vertex, DEdge> g;
    private List<Vertex> entryPoints = new ArrayList<Vertex>();

    public GraphBuilder(Ast t){
        try {
            this.initialize(t);
        } catch (BuildDependencyGraphException e) {
            e.printStackTrace();
        }
    }

    public void initialize(Ast ast) throws BuildDependencyGraphException {
        //this.g = new DefaultDirectedGraph<>((v1, v2) -> new DEdge(v1, v2));
        this.g = new DirectedMultigraph<>((v1, v2) -> new DEdge(v1, v2));

        //############################################################################
        Map<String,AttributeTask> tasks = new TreeMap<String,AttributeTask>();
        for(Ast.Entity entity : ast.getEntities()) {
            Vertex entityTask = new EntityTask(entity.getName());
            entryPoints.add(entityTask);
            //g.addVertex(entityTask);
            AttributeTask oid = new AttributeTask(entity,new Ast.Attribute("oid", Types.DATATYPE.INTEGER,new Ast.Generator("IdGenerator")));
            tasks.put(entity.getName()+".oid", oid);
            g.addVertex(oid);
            g.addVertex(entityTask);
            for(Ast.Attribute attribute : entity.getAttributes()) {
                AttributeTask task = new AttributeTask(entity,attribute);
                tasks.put(task.getEntity()+"."+task.getAttributeName(),task);
                g.addVertex(task);
                //g.addEdge(task,oid); Reversed due to topological sorting implementation
                g.addEdge(oid,task);
                g.addEdge(task, entityTask);

            }
        }
        //############################################################################

        Set<AttributeTask> processed = new TreeSet<AttributeTask>((t1, t2) -> { return t1.toString().compareTo(t2.toString());});
        for(Map.Entry<String,AttributeTask> task : tasks.entrySet() ) {
            if(task.getKey().substring(task.getKey().length()-3, task.getKey().length()).equalsIgnoreCase("oid")){
                //
            }else if( !processed.contains(task.getValue())) {
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
                            //g.addEdge(currentTask,otherTask); Reversed due to topological sorting implementation
                            g.addEdge(otherTask,currentTask);
                        }
                    }
                }
            }
        }
        if(processed.size() != tasks.size()) throw new BuildDependencyGraphException("Dependency plan wrongly built. Missing nodes");
    }
    public DirectedGraph<Vertex, DEdge> getG() {
        return g;
    }

    public void setG(DirectedGraph<Vertex, DEdge> g) {
        this.g = g;
    }
}
