package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;

/**
 * Created by aprat on 1/09/16.
 */
public class Literal extends Vertex {

    public <T> Literal(T value) {
        properties.put("value", new PropertyValue(value));
    }

    public String getValue() {
        return properties.get("value").getValue();
    }

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
