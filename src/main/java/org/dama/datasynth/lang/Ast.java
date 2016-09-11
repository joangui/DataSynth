package org.dama.datasynth.lang;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.semanticcheckpasses.AttributeValidName;
import org.dama.datasynth.lang.semanticcheckpasses.EdgeEndpointsExist;
import org.dama.datasynth.lang.semanticcheckpasses.GeneratorsExist;
import org.dama.datasynth.lang.semanticcheckpasses.GeneratorRunParametersValid;

import java.util.*;

/**
 * Created by aprat on 10/04/16.
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

        public Generator getGenerator() {
            return generator;
        }

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

    public static class Atomic extends Node {

        private Types.DataType dataType = null;

        public Atomic(String name, Types.DataType dataType) {
            super(name);
            this.dataType = dataType;
        }

        @Override
        public void accept(AstVisitor visitor) {
            visitor.visit(this);
        }

        public Types.DataType getDataType() {
            return dataType;
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
         * Class Constructor
         * @param name The name of the generator
         */
        public Generator(String name) {
            super(name);
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
        private Generator       correllation = null;

        /**
         * Class Constructor
         * @param name The name of the generator
         */
        public Edge(String name, String source, String target, Types.Direction direction) {
            super(name);
            this.source = source;
            this.target = target;
            this.direction = direction;
        }

        public String getSource() {
            return source;
        }

        public String getTarget() {
            return target;
        }

        public Types.Direction getDirection() {
            return direction;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public void setDirection(Types.Direction direction) {
            this.direction = direction;
        }

        public Generator getSourceCardinalityGenerator() {
            return sourceCardinalityGenerator;
        }

        public void setSourceCardinalityGenerator(Generator sourceCardinalityGenerator) {
            this.sourceCardinalityGenerator = sourceCardinalityGenerator;
        }

        public Generator getTargetCardinalityGenerator() {
            return targetCardinalityGenerator;
        }

        public void setTargetCardinalityGenerator(Generator targetCardinalityGenerator) {
            this.targetCardinalityGenerator = targetCardinalityGenerator;
        }

        public Long getSourceCardinalityNumber() {
            return sourceCardinalityNumber;
        }

        public void setSourceCardinalityNumber(Long sourceCardinalityNumber) {
            this.sourceCardinalityNumber = sourceCardinalityNumber;
        }

        public Long getTargetCardinalityNumber() {
            return targetCardinalityNumber;
        }

        public void setTargetCardinalityNumber(Long targetCardinalityNumber) {
            this.targetCardinalityNumber = targetCardinalityNumber;
        }

        public Generator getCorrellation() {
            return correllation;
        }

        public void setCorrellation(Generator correllation) {
            this.correllation = correllation;
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
        AttributeValidName attributeValidName = new AttributeValidName();
        attributeValidName.check(this);
        GeneratorsExist generatorExists = new GeneratorsExist();
        generatorExists.check(this);
        GeneratorRunParametersValid generatorRunParametersValid = new GeneratorRunParametersValid();
        generatorRunParametersValid.check(this);
    }
}
