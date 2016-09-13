package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;

/**
 * Created by aprat on 20/04/16.
 * Represents an attribute element in the dependency graph
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

    @Override
    public void accept(DependencyGraphVisitor visitor) {
        visitor.visit(this);
    }
}
