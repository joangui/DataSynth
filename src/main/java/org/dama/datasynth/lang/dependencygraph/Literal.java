package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;

/**
 * Created by aprat on 1/09/16.
 * Represents a literal in the dependency graph
 */
public class Literal extends Vertex {

    /**
     * Constructor
     * @param value The Value of the literal
     * @param <T> The Type of the literal
     */
    public <T> Literal(T value) {
        properties.put("value", new PropertyValue(value));
    }


    /**
     * Gets the value of the literal in a string
     * @return The value of the literal in a string
     */
    public String getValue() {
        return properties.get("value").getValue();
    }

    /**
     * Gets the data type of the literal
     * @return The data type of the literal
     */
    public Types.DataType getDataType() {
        return properties.get("value").getDataType();
    }


    @Override
    public String toString(){
        return "[" + getValue() + ","+getDataType().getText()+","+getClass().getSimpleName()+"]";
    }

    @Override
    public void accept(DependencyGraphVisitor visitor) {
        visitor.visit(this);
    }


}
