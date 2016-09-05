package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;

/**
 * Created by aprat on 20/04/16.
 */
public class Attribute extends Vertex {

    private String          attributeName   = null;
    private Types.DataType dataType        = null;

    /**
     * Class Constructor
     * @param attributeName The name of the attribute
     * @param dataType The type of the attribute
     */
    public Attribute(String attributeName, Types.DataType dataType ) {
        super();
        this.attributeName = attributeName;
        this.dataType = dataType;
    }

    /**
     * Gets the generator of this attribute task
     * @return The name of the generator of this attribute task
     */
    @Schnappi(name="generator")
    public String getGenerator() {
        /*for(DirectedEdge edge : graph.outgoingEdgesOf(this)) {
            if(edge.getName().compareTo("generator") == 0) {
                return ((Generator)graph.getEdgeTarget(edge)).getName();
            }
        }*/
        throw new DependencyGraphConstructionException("Dependency graph is not correctly built. Attribute is missing generator");
    }


    /**
     * Gets the name of the attribute this task is generating something for
     * @return The attribute
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Gets the type of the attribute this task is generating something for
     * @return The attribute
     */
    public Types.DataType getDataType() {
        return dataType;
    }

    @Override
    public String toString(){
        return "[" + getAttributeName() + ","+getClass().getSimpleName()+"]";
    }

    public void accept(DependencyGraphVisitor visitor) {
        visitor.visit(this);
    }
}
