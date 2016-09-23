package org.dama.datasynth.lang.dependencygraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DirectedMultigraph;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by quim on 5/12/16.
 * This class represents a dependency graph, which is modeled completely as a property graph, and used to represent
 * the dependencies between the different elements to generate.
 */
public class DependencyGraph  {

    private Map<String,Entity>      entities    = null;
    private Map<String,Attribute>   attributes  = null;
    private Map<String,Edge>        edges       = null;
    private List<Literal>           literals    = null;
    private List<Generator>         generators  = null;


    private DirectedMultigraph<Vertex, DirectedEdge> graph = null;

    /**
     * Constructor
     */
    public DependencyGraph() {
        graph = new DirectedMultigraph<Vertex,DirectedEdge>((v1, v2) -> new DirectedEdge("UNDEFINED"));
        entities = new HashMap<String,Entity>();
        attributes = new HashMap<String,Attribute>();
        edges = new HashMap<String,Edge>();
        literals = new ArrayList<Literal>();
        generators = new ArrayList<Generator>();
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
        if(this.attributes.put(attribute.getName(), attribute) != null) throw new DependencyGraphConstructionException("Attribute with name "+attribute.getName()+" already exists.");
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
        generators.add(generator);
    }

    /**
     * Adds a Vertex of type Literal into the graph
     * @param literal The Literal to add
     */
    public void addLiteralVertex(Literal literal) {
        graph.addVertex(literal);
        literals.add(literal);
    }

    /**
     * Gets the Source of an edge
     * @param edge The edge to get the source from
     * @return The source vertex of the edge.
     */
    public Vertex getEdgeSource(DirectedEdge edge) {
        return graph.getEdgeSource(edge);
    }

    /**
     * Gets the Target of an edge
     * @param edge The edge to get the target from
     * @return The target vertex of the edge.
     */
    public Vertex getEdgeTarget(DirectedEdge edge) {
        return graph.getEdgeTarget(edge);
    }

    /**
     * Gets the edges of a Vertex
     * @param vertex The Vertex to retrieve the edges from
     * @return A list with the edges of the Vertex
     */
    public List<DirectedEdge> getEdges(Vertex vertex) {
        if(!graph.containsVertex(vertex)) throw new DependencyGraphConstructionException("Error when querying the dependency graph. Vertex "+vertex.getId()+" of type "+vertex.getType()+" does not exist");
        Set<DirectedEdge> edges = graph.outgoingEdgesOf(vertex);
        List<DirectedEdge> neighbors = new ArrayList<DirectedEdge>();
        for(DirectedEdge edge : edges) {
            neighbors.add(edge);
        }
        return neighbors;
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
     * Gets the list of edges in the dependency graph
     * @return The list of edges.
     */
    public List<Edge> getEdges() {
        return new ArrayList(edges.values());
    }

    /**
     * Gets the list of attributes of the graph
     * @return
     */
    public List<Attribute> getAttributes() {
        return new ArrayList(attributes.values());
    }

    /**
     * Gets the literals inserted in the graph
     * @return The list of literals in the graph;
     */
    public List<Literal> getLiterals() {
        return literals;
    }

    /**
     * Gets the generators inserted in the graph
     * @return The generators inserted in the graph
     */
    public List<Generator> getGenerators() {
        return generators;
    }

    public void visualize() {
        JFrame frame = new JFrame("Dependency Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JGraphXAdapter<Vertex,DirectedEdge> adapter = new JGraphXAdapter<Vertex, DirectedEdge>(graph);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(adapter);
        Object [] entities = new Object[getEntities().size()];
        int index = 0;
        for(Entity entity : getEntities()) {
            entities[index] = adapter.getVertexToCellMap().get(entity);
            index++;
        }
        adapter.setCellStyle("fontColor=black;fillColor=green",entities);
        Object [] attributes = new Object[getAttributes().size()];
        index = 0;
        for(Attribute attribute : getAttributes()) {
            attributes[index] = adapter.getVertexToCellMap().get(attribute);
            index++;
        }
        adapter.setCellStyle("fontColor=black;fillColor=red",attributes);

        Object [] edges = new Object[getEdges().size()];
        index = 0;
        for(Edge edge : getEdges()) {
            edges[index] = adapter.getVertexToCellMap().get(edge);
            index++;
        }
        adapter.setCellStyle("fontColor=black;fillColor=lightblue",edges);

        Object [] literals = new Object[getLiterals().size()];
        index = 0;
        for(Literal literal : getLiterals()) {
            literals[index] = adapter.getVertexToCellMap().get(literal);
            index++;
        }
        adapter.setCellStyle("fontColor=black;fillColor=yellow",literals);

        Object [] generators = new Object[getGenerators().size()];
        index = 0;
        for(Generator generator : getGenerators()) {
            generators[index] = adapter.getVertexToCellMap().get(generator);
            index++;
        }
        adapter.setCellStyle("fontColor=black;fillColor=orange",generators);



        layout.execute(adapter.getDefaultParent());
        frame.add(new mxGraphComponent(adapter));
        frame.pack();
        frame.setSize(800, 800);
        frame.setLocation(300, 200);
        frame.setVisible(true);
    }
}
