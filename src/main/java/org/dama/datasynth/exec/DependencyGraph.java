package org.dama.datasynth.exec;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.utils.traversals.Tree;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

/**
 * Created by quim on 5/12/16.
 */
public class DependencyGraph  extends DirectedMultigraph<Vertex,DEdge> implements Tree<Vertex> {

    private List<Vertex> entryPoints = new ArrayList<Vertex>();

    /**
     * Constructor
     * @param ast The ast to build the dependency graph from
     * @throws Exception
     */
    public DependencyGraph(Ast ast) {
        super((v1, v2) -> new DEdge(v1, v2));
        initialize(ast);
    }


    /**
     * Initializes the dependency graph given an Ast
     * @param ast The Ast to initialize the dependency graph from
     */
    private void initialize(Ast ast) {
        //############################################################################
        Map<String,AttributeTask> tasks = new TreeMap<String,AttributeTask>();
        for(Ast.Entity entity : ast.getEntities()) {
            Vertex entityTask = new EntityTask(this,entity.getName());
            entryPoints.add(entityTask);
            //g.addVertex(entityTask);
            AttributeTask oid = new AttributeTask(this,entity,new Ast.Attribute("oid", Types.DATATYPE.INTEGER, new Ast.Generator("IdGenerator")));
            tasks.put(entity.getName()+".oid", oid);
            addVertex(oid);
            addVertex(entityTask);
            addEdge(oid,entityTask);
            for(Ast.Attribute attribute : entity.getAttributes()) {
                AttributeTask task = new AttributeTask(this,entity,attribute);
                tasks.put(task.getEntity().getName()+"."+task.getAttributeName(),task);
                addVertex(task);
                addEdge(oid,task);
                addEdge(task, entityTask);
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
                            addEdge(otherTask,currentTask);
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
            EdgeTask et = new EdgeTask(this,edge,source, target, sourceAttributes, targetAttributes);
            addVertex(et);
            addEdge(source,et);
            addEdge(target,et);
        }
    }

    @Override
    public List<Vertex> getRoots() {
        return entryPoints;
    }

    /**
     * Prints the dependency graph
     */
    /*public void print(){
        for(DEdge e: edgeSet()){
            System.out.println(e.getSource().getType() + " :: " + e.getTarget().getType());
            System.out.println(e.getSource().getId() + " <- " + e.getTarget().getId());
        }
    }*/
}
