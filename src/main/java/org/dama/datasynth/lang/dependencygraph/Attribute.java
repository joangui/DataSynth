package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;

/**
 * Created by aprat on 20/04/16.
 */
public class Attribute extends Vertex {

    /**
     * Class Constructor
     * @param attributeName The name of the attribute
     * @param dataType The type of the attribute
     */
    public Attribute(String attributeName, Types.DataType dataType ) {
        super();
        properties.put("name",new PropertyValue(attributeName));
        properties.put("datatype",new PropertyValue(dataType.getText()));
    }

    /**
     * Gets the generator of this attribute task
     * @return The name of the generator of this attribute task
     */
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
    public String getName() {
        return properties.get("name").getValue();
    }

    /**
     * Gets the type of the attribute this task is generating something for
     * @return The attribute
     */
    public Types.DataType getDataType() {
        return Types.DataType.fromString(properties.get("datatype").getValue());
    }

    @Override
    public String toString(){
        return "[" + getName() + ","+getClass().getSimpleName()+"]";
    }

    public void accept(DependencyGraphVisitor visitor) {
        visitor.visit(this);
    }
}
