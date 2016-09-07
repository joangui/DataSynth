package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

/**
 * Created by quim on 5/12/16.
 */
public class DependencyGraph  {

    private Map<String,Entity>      entities    = null;
    private Map<String,Attribute>   attributes  = null;
    private Map<String,Edge>        edges       = null;

    private DirectedMultigraph<Vertex, DirectedEdge> graph = null;

    /**
     * Constructor
     */
    public DependencyGraph() {
        graph = new DirectedMultigraph<Vertex,DirectedEdge>((v1, v2) -> new DirectedEdge("UNDEFINED"));
        entities = new HashMap<String,Entity>();
        attributes = new HashMap<String,Attribute>();
        edges = new HashMap<String,Edge>();
    }

    /**
     * Adds a Vertex of type Entity into the graph
     * @param entity The Entity to add
     */
    public void addEntityVertex(Entity entity ) {
        if(this.entities.put(entity.getName(), entity) != null) throw new DependencyGraphConstructionException("Entity with name "+entity.getName()+" already exists.");
        graph.addVertex(entity);
    }

    /**
     * Adds a Vertex of type Attribute into the graph
     * @param attribute The Attribute to add
     */
    public void addAttributeVertex(Attribute attribute ) {
        if(this.attributes.put(attribute.getAttributeName(), attribute) != null) throw new DependencyGraphConstructionException("Attribute with name "+attribute.getAttributeName()+" already exists.");
        graph.addVertex(attribute);
    }

    /**
     * Adds a Vertex of type Edge into the graph
     * @param edge The Edge to add
     */
    public void addEdgeVertex(Edge edge) {
        if(this.edges.put(edge.getName(), edge) != null) throw new DependencyGraphConstructionException("Edge with name "+edge.getName()+" already exists.");
        graph.addVertex(edge);
    }

    /**
     * Adds a Vertex of type Generator into the graph
     * @param generator The Generator to add
     */
    public void addGeneratorVertex(Generator generator) {
        graph.addVertex(generator);
    }

    /**
     * Adds a Vertex of type Literal into the graph
     * @param literal The Literal to add
     */
    public void addLiteralVertex(Literal literal) {
        graph.addVertex(literal);
    }

    /**
     * Gets the neighbors of a Vertex
     * @param vertex The Vertex to retrieve the neighbors from
     * @return A list with the neighbors of the Vertex
     */
    public List<Vertex> getNeighbors(Vertex vertex) {
        if(!graph.containsVertex(vertex)) throw new DependencyGraphConstructionException("Error when querying the dependency graph. Vertex "+vertex.getId()+" of type "+vertex.getType()+" does not exist");
        Set<DirectedEdge> edges = graph.outgoingEdgesOf(vertex);
        List<Vertex> neighbors = new ArrayList<Vertex>();
        for(DirectedEdge edge : edges) {
            neighbors.add(graph.getEdgeTarget(edge));
        }
        return neighbors;
    }

    /**
     * Gets the neighbros of a Vertex connected with edge with a given label
     * @param vertex The Vertex to query the neighbors from
     * @param label The label of the edge
     * @return The list of neighbors connected with edges with the given label.
     */
    public List<Vertex> getNeighbors(Vertex vertex, String label) {
        if(!graph.containsVertex(vertex)) throw new DependencyGraphConstructionException("Error when querying the dependency graph. Vertex "+vertex.getId()+" of type "+vertex.getType()+" does not exist");
        Set<DirectedEdge> edges = graph.outgoingEdgesOf(vertex);
        List<Vertex> neighbors = new ArrayList<Vertex>();
        for(DirectedEdge edge : edges) {
            if(edge.getName().compareTo(label) == 0) {
                neighbors.add(graph.getEdgeTarget(edge));
            }
        }
        return neighbors;
    }

    /**
     * Gets the incoming neighbors of a Vertex
     * @param vertex The Vertex to retrieve the neighbors from
     * @return A list with the neighbors of the Vertex
     */
    public List<Vertex> getIncomingNeighbors(Vertex vertex) {
        if(!graph.containsVertex(vertex)) throw new DependencyGraphConstructionException("Error when querying the dependency graph. Vertex "+vertex.getId()+" of type "+vertex.getType()+" does not exist");
        Set<DirectedEdge> edges = graph.incomingEdgesOf(vertex);
        List<Vertex> neighbors = new ArrayList<Vertex>();
        for(DirectedEdge edge : edges) {
            neighbors.add(graph.getEdgeSource(edge));
        }
        return neighbors;
    }


    /**
     * Gets the incoming neighbros of a Vertex connected with edge with a given label
     * @param vertex The Vertex to query the neighbors from
     * @param label The label of the edge
     * @return The list of incoming neighbors connected with edges with the given label.
     */
    public List<Vertex> getIncomingNeighbors(Vertex vertex, String label) {
        if(!graph.containsVertex(vertex)) throw new DependencyGraphConstructionException("Error when querying the dependency graph. Vertex "+vertex.getId()+" of type "+vertex.getType()+" does not exist");
        Set<DirectedEdge> edges = graph.incomingEdgesOf(vertex);
        List<Vertex> neighbors = new ArrayList<Vertex>();
        for(DirectedEdge edge : edges) {
            if(edge.getName().compareTo(label) == 0) {
                neighbors.add(graph.getEdgeSource(edge));
            }
        }
        return neighbors;
    }


    /**
     * Gets the Entity Vertex with the given name
     * @param entityName The name of the entity
     * @return The Entity Vertex. null if the entity does not exist
     */
    public Entity getEntity(String entityName) {
        return entities.get(entityName);
    }

    /**
     * Gets the Attribute Vertex with the given name
     * @param attributeName The name of the attribute
     * @return The Attribute Vertex. null if the attribute does not exist
     */
    public Attribute getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    /**
     * Gets the Edge Vertex with the given name
     * @param edgeName The name of the Edge
     * @return The Edge Vertex. null if the Edge does not exist
     */
    public Edge getEdge(String edgeName) {
        return edges.get(edgeName);
    }

    /**
     * Adds a dependency between two vertices
     * @param source The source vertex
     * @param target The edge vertex
     * @param label The
     */
    public void addDependency(Vertex source, Vertex target, String label) {
        if(!graph.containsVertex(source)) throw new DependencyGraphConstructionException("Error when adding a dependency. Source vertex "+source.getId()+" of type "+source.getType()+" does not exist");
        if(!graph.containsVertex(target)) throw new DependencyGraphConstructionException("Error when adding a dependency. Target vertex "+target.getId()+" of type "+target.getType()+" does not exist");
        graph.addEdge(source,target, new DirectedEdge(label));
    }

    /**
     * Gets the list of entities in the dependency graph
     * @return The list of entities.
     */
    public List<Entity> getEntities() {
        return new ArrayList(entities.values());
    }

    /**
     * Initializes the dependency graph given an Ast
     * @param ast The Ast to initialize the dependency graph from
     */
    /*private void initialize(Ast ast) {
        Map<String,Attribute> tasks = new TreeMap<String,Attribute>();
        for(Ast.Entity entity : ast.getEntities()) {
            Vertex entityTask = new Entity(this,entity.getName());
            entities.add(entityTask);
            //g.addVertex(entityTask);
            Attribute oid = new Attribute(this,entity,new Ast.Attribute("oid", Types.DataType.INTEGER, new Ast.Generator("IdGenerator")));
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

    */
}
