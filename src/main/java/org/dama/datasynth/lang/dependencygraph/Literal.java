package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;

/**
 * Created by aprat on 1/09/16.
 */
public class Literal extends Vertex {

    public Literal(String value, Types.DataType dataType ) {
        properties.put("value", new PropertyValue(value));
        properties.put("datatype",new PropertyValue(dataType.getText()));
    }

    public String getValue() {
        return properties.get("value").getValue();
    }

    public Types.DataType getDataType() {
        return Types.DataType.fromString(properties.get("datatype").getValue());
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
