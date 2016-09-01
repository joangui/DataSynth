package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

/**
 * Created by quim on 5/12/16.
 */
public class DependencyGraph  extends DirectedMultigraph<Vertex,DEdge> {

    private List<Vertex> entities = new ArrayList<Vertex>();

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
        Map<String,Attribute> tasks = new TreeMap<String,Attribute>();
        for(Ast.Entity entity : ast.getEntities()) {
            Vertex entityTask = new Entity(this,entity.getName());
            entities.add(entityTask);
            //g.addVertex(entityTask);
            Attribute oid = new Attribute(this,entity,new Ast.Attribute("oid", Types.DATATYPE.INTEGER, new Ast.Generator("IdGenerator")));
            tasks.put(entity.getName()+".oid", oid);
            addVertex(oid);
            addVertex(entityTask);
            addEdge(entityTask,oid);
            for(Ast.Attribute attribute : entity.getAttributes()) {
                Attribute task = new Attribute(this,entity,attribute);
                tasks.put(task.getEntity().getName()+"."+task.getAttributeName(),task);
                addVertex(task);
                addEdge(task,oid);
                addEdge(entityTask,task);
            }
        }
        //############################################################################

        Set<Attribute> processed = new TreeSet<Attribute>((t1, t2) -> { return t1.toString().compareTo(t2.toString());});
        for(Map.Entry<String,Attribute> task : tasks.entrySet() ) {
            if(task.getKey().substring(task.getKey().length()-3, task.getKey().length()).equalsIgnoreCase("oid")){
                processed.add(task.getValue());
            }else if( !processed.contains(task.getValue())) {
                List<Attribute> toProcess = new LinkedList<Attribute>();
                toProcess.add(task.getValue());
                while(!toProcess.isEmpty()) {
                    Attribute currentTask = toProcess.get(0);
                    toProcess.remove(0);
                    processed.add(currentTask);
                    for (String param : currentTask.getRunParameters()) {
                        Attribute otherTask = tasks.get(currentTask.getEntity().getName()+"."+param);
                        if (otherTask != null) {
                            if (!processed.contains(otherTask)) {
                                toProcess.add(otherTask);
                            }
                            addEdge(currentTask,otherTask);
                        }
                    }
                }
            }
        }
        System.out.println("Processed " + processed.size() + " Tasks " + tasks.size());
        if(processed.size() != tasks.size()) throw new RuntimeException("Critical internal Error. Dependency plan wrongly built. Some nodes might be missing");
        //Now process the edges which represent relationships between different entities

        Map<String, Entity> entities = new HashMap<>();
        for(Vertex vtx : this.entities) {
            entities.put(vtx.getId(), (Entity) vtx);
        }

        for(Ast.Edge edge : ast.getEdges()) {
            Entity entity = entities.get(edge.getEntity().getName());
            ArrayList<Attribute> attributes = new ArrayList<Attribute>();
            String orig = edge.getEntity().getName();
            for(Ast.Attribute attr : edge.getEntity().getAttributes()) attributes.add(tasks.get(orig+"."+attr.getName()));
            Edge et = new Edge(this, edge, entity, attributes);
            addVertex(et);
            addEdge(et,entity);
        }
    }

    public List<Vertex> getEntities() {
        return entities;
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
