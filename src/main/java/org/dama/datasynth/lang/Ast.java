package org.dama.datasynth.lang;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.semanticcheckpasses.*;

import java.util.*;

/**
 * Created by aprat on 10/04/16.
 * Class representing an ast of the query language
 */
public class Ast {

    /**
     * A node of the AST
     */
    public static abstract class Node {

        /**
         * The name of the AST node
         */
        protected String           name       = null;

        /**
         * Class Constructor
         * @param name The name of the AST Node
         */
        public Node(String name) {
            this.name = name;
        }

        /**
         * Gets the name of the node
         * @return The name of the node
         */
        public String getName() {
            return name;
        }

        /**
         * Accept method used by the visitors when the exact type of the node is not available.
         * @param visitor The visitor visiting the node.
         */
        public abstract void accept(AstVisitor visitor);

    }

    /**
     * An attribute in the AST
     */
    public static class Attribute extends Node {

        /**
         * The data type of the attribute.
         */
        private Types.DataType type;

        /**
         * The generator to generate this attribute
         */
        private Generator generator;

        /**
         * Class Constructor
         * @param name The name of the attribute.
         * @param type The data type of the attribute.
         */
        public Attribute(String name, Types.DataType type, Generator generator) {
            super(name);
            this.type = type;
            this.generator = generator;
        }

        /**
         * Gets the generator node of this attribute
         * @return The generator node
         */
        public Generator getGenerator() {
            return generator;
        }

        /**
         * Gets the data type of the attribute
         * @return The data type of the attribute
         */
        public Types.DataType getType() {
            return type;
        }

        @Override
        public void accept(AstVisitor visitor) {
            visitor.visit(this);
        }
    }

    /**
     * An entity in the AST
     */
    public static class Entity extends Node {

        /**
         * The list of attributes of the entity
         */
        protected Map<String, Attribute> attributes = new HashMap<String,Attribute>();

        /**
         * The number of entities
         */
        protected Long numInstances;

        /**
         * Gets the number of entities
         * @return The number of entities
         */
        public Long getNumInstances() {
            return numInstances;
        }

        /** Class Constructor
         * @param name The name of the entity
         */
        public Entity(String name, Long numInstances) {
            super(name);
            this.numInstances = numInstances;
        }

        /**
         * Get the attributes of the entity
         * @return The attributes of the entity
         */
        public Map<String, Attribute> getAttributes() { return attributes; }

        /**
         * Adds a new attribute to the entity
         * @param attribute The attribute to be added
         */
        public void addAttribute(Attribute attribute) {
            if(attributes.put(attribute.getName(),attribute) != null)  throw new SemanticException(SemanticException.SemanticExceptionType.ATTRIBUTE_NAME_REPEATED,attribute.getName());
        }


        @Override
        public void accept(AstVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class  Atomic<T> extends Node {

        private T element = null;

        public Atomic(T element) {
            super(element.toString());
            this.element = element;
        }

        public T getElement() {
            return element;
        }

        @Override
        public void accept(AstVisitor visitor) {
            visitor.visit(this);
        }

        /**
         * Gets the data type of the atomic
         * @return The data type of the atomic.
         */
        public Types.DataType getDataType() {
            return Types.DataType.fromObject(element);
        }
    }

    /**
     * A generator in the AST
     */
    public static class Generator extends Node {

        /**
         * The list of parameters of the init method of the generator.
         */
        private List<Atomic> initParameters = new ArrayList<Atomic>();

        /**
         * The list of parameters of the run method of the generator
         */
        private List<Atomic> runParameters = new ArrayList<Atomic>();

        /**
         * The return type of the generator's run function.
         */
        private Types.DataType returnType = null;

        /**
         * Class Constructor
         * @param name The name of the generator
         */
        public Generator(String name, Types.DataType returnType) {
            super(name);
            this.returnType = returnType;
        }

        @Override
        public void accept(AstVisitor visitor) {
            visitor.visit(this);
        }

        /**
         * Gets the list of parameters of the run method for this generator
         */
        public List<Atomic> getRunParameters() { return runParameters; }

        /**
         * Gets the list of parameters for the init method this generator
         */
        public List<Atomic> getInitParameters() { return initParameters; }

        /**
         * Adds a run method parameter
         * @param parameter The parameter to add
         */
        public void addRunParameter(Atomic parameter)  { runParameters.add(parameter);}

        /**
         * Adds an init method parameter
         * @param parameter The parameter to add
         */
        public void addInitParameter(Atomic parameter)  { initParameters.add(parameter);}


        /**
         * Gets the return type of the generator's run function
         * @return The return type.
         */
        public Types.DataType getReturnType() {
            return returnType;
        }
    }

    /**
     * A generator in the AST
     */
    public static class Edge extends Node {

        private String          source = null;
        private String          target = null;
        private Types.Direction direction = null;
        private Generator       sourceCardinalityGenerator = null;
        private Generator       targetCardinalityGenerator = null;
        private Long            sourceCardinalityNumber = null;
        private Long            targetCardinalityNumber = null;
        private List<Atomic>    correlates = null;

        /**
         * Class Constructor
         * @param name The name of the generator
         */
        public Edge(String name, String source, String target, Types.Direction direction) {
            super(name);
            this.source = source;
            this.target = target;
            this.direction = direction;
            this.correlates = new ArrayList<Atomic>();
        }

        /**
         * Gets the name of the source entity of the edge
         * @return The name of the source entity of the edge
         */
        public String getSource() {
            return source;
        }

        /**
         * Gets the name of the target entity of the edge.
         * @return The name of the target entity of the edge.
         */
        public String getTarget() {
            return target;
        }

        /**
         * Gets the direction of the edge
         * @return The direction of the edge
         */
        public Types.Direction getDirection() {
            return direction;
        }

        /**
         * Gets the source cardinality generator of the edge
         * @return The source cardinality generator of the edge
         */
        public Generator getSourceCardinalityGenerator() {
            return sourceCardinalityGenerator;
        }

        /**
         * Sets the source cardinality generator of the edge
         * @param sourceCardinalityGenerator The source cardinality generator
         */
        public void setSourceCardinalityGenerator(Generator sourceCardinalityGenerator) {
            this.sourceCardinalityGenerator = sourceCardinalityGenerator;
        }

        /**
         * Gets the target cardinality generator of the edge
         * @return The target cardinality generator of the edge
         */
        public Generator getTargetCardinalityGenerator() {
            return targetCardinalityGenerator;
        }

        /**
         * Sets the target cardinality generator of the edge
         * @param targetCardinalityGenerator The target cardinality generator
         */
        public void setTargetCardinalityGenerator(Generator targetCardinalityGenerator) {
            this.targetCardinalityGenerator = targetCardinalityGenerator;
        }

        /**
         * Gets the source cardinality number of the generator
         * @return
         */
        public Long getSourceCardinalityNumber() {
            return sourceCardinalityNumber;
        }

        /**
         * Sets the source cardinality number of the generator
         * @param sourceCardinalityNumber The source cardinality number to set
         */
        public void setSourceCardinalityNumber(Long sourceCardinalityNumber) {
            this.sourceCardinalityNumber = sourceCardinalityNumber;
        }

        /**
         * Gets the target cardinality number of the generator
         * @return
         */
        public Long getTargetCardinalityNumber() {
            return targetCardinalityNumber;
        }

        /**
         * Sets the target cardinality number of the generator
         * @param targetCardinalityNumber The target cardinality number to set
         */
        public void setTargetCardinalityNumber(Long targetCardinalityNumber) {
            this.targetCardinalityNumber = targetCardinalityNumber;
        }

        /**
         * Gets the list of the correlates attributes
         * @return The list of correlates attributes
         */
        public List<Ast.Atomic> getCorrelates() {
            return correlates;
        }

        /**
         * Add an attribute to the list of the correlates attributes
         * @param correlates The attribute to add.
         */
        public void addCorrelates(Atomic correlates) {
            this.correlates.add(correlates);
        }


        @Override
        public void accept(AstVisitor visitor) {
            visitor.visit(this);
        }
    }

    private Map<String,Entity>      entities = new HashMap<String,Entity>();
    private Map<String,Edge>        edges = new HashMap<String,Edge>();

    /**
     * Gets the list of entities of the AST
     */
    public Map<String, Entity> getEntities() { return entities; }

    /**
     * Adds a new entity into the AST
     */
    protected void addEntity( Entity entity ) { entities.put(entity.getName(),entity); }


    /**
     * Adds a new edge into the AST
     */
    protected void addEdge(Edge edge) { edges.put(edge.getName(), edge); }

    /**
     * Gets the list of edges of the AST
     */
    public Map<String, Edge> getEdges() { return edges; }


    /**
     * Performs a semantic analysis over the AST.
     * Checks for valid parameter names for generators
     * @throws SemanticException
     */
    public void doSemanticAnalysis() throws SemanticException {
        EdgeEndpointsExist edgeEndpointsExist = new EdgeEndpointsExist();
        edgeEndpointsExist.check(this);
        EdgeCorrelatesValid edgeCorrelatesValid = new EdgeCorrelatesValid();
        edgeCorrelatesValid.check(this);
        AttributeValidName attributeValidName = new AttributeValidName();
        attributeValidName.check(this);
        GeneratorsExist generatorExists = new GeneratorsExist();
        generatorExists.check(this);
        GeneratorRequiresValid generatorRequiresValid = new GeneratorRequiresValid();
        generatorRequiresValid.check(this);
    }
}
