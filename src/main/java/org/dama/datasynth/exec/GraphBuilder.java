package org.dama.datasynth.exec;

import org.dama.datasynth.DataSynth;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;
import scala.tools.nsc.backend.icode.TypeKinds;

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

    public GraphBuilder(Ast t) {
            this.initialize(t);
    }

    public void initialize(Ast ast) {
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
                tasks.put(task.getEntity().getName()+"."+task.getAttributeName(),task);
                g.addVertex(task);
                g.addEdge(oid,task);
                g.addEdge(task, entityTask);
            }
        }
        //############################################################################

        Set<AttributeTask> processed = new TreeSet<AttributeTask>((t1, t2) -> { return t1.toString().compareTo(t2.toString());});
        for(Map.Entry<String,AttributeTask> task : tasks.entrySet() ) {
            if(task.getKey().substring(task.getKey().length()-3, task.getKey().length()).equalsIgnoreCase("oid")){
                processed.add(task.getValue());
            }else if( !processed.contains(task.getValue())) {
                List<AttributeTask> toProcess = new LinkedList<AttributeTask>();
                toProcess.add(task.getValue());
                while(!toProcess.isEmpty()) {
                    AttributeTask currentTask = toProcess.get(0);
                    toProcess.remove(0);
                    processed.add(currentTask);
                    for (String param : currentTask.getRunParameters()) {
                        AttributeTask otherTask = tasks.get(currentTask.getEntity().getName()+"."+param);
                        if (otherTask != null) {
                            if (!processed.contains(otherTask)) {
                                toProcess.add(otherTask);
                            }
                            g.addEdge(otherTask,currentTask);
                        }
                    }
                }
            }
        }
        System.out.println("Processed " + processed.size() + " Tasks " + tasks.size());
        if(processed.size() != tasks.size()) throw new RuntimeException("Critical internal Error. Dependency plan wrongly built. Some nodes might be missing");
        //Now process the edges which represent relationships between different entities
        Map<String, EntityTask> entities = new HashMap<>();
        for(Vertex vtx : entryPoints) {
            entities.put(vtx.getId(), (EntityTask) vtx);
        }
        for(Ast.Edge edge : ast.getEdges()) {
            EntityTask source = entities.get(edge.getOrigin().getName());
            EntityTask target = entities.get(edge.getDestination().getName());
            ArrayList<AttributeTask> sourceAttributes = new ArrayList<AttributeTask>();
            ArrayList<AttributeTask> targetAttributes = new ArrayList<AttributeTask>();
            String orig = edge.getOrigin().getName();
            String dest = edge.getDestination().getName();
            for(Ast.Attribute attr : edge.getOrigin().getAttributes()) sourceAttributes.add(tasks.get(orig+"."+attr.getName()));
            for(Ast.Attribute attr : edge.getDestination().getAttributes()) targetAttributes.add(tasks.get(dest+"."+attr.getName()));
            EdgeTask et = new EdgeTask(edge,source, target, sourceAttributes, targetAttributes);
            g.addVertex(et);
            g.addEdge(source,et);
            g.addEdge(target,et);
        }
    }
    public DirectedGraph<Vertex, DEdge> getG() {
        return g;
    }

    public void setG(DirectedGraph<Vertex, DEdge> g) {
        this.g = g;
    }
}
