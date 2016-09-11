package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;

/**
 * Created by quim on 4/27/16.
 */
public class Edge extends Vertex {



    public Edge(String name, Types.Direction direction) {
        super();
        properties.put("name",new PropertyValue(name));
        properties.put("direction",new PropertyValue(direction.getText()));
    }

    public String getName() {
        return properties.get("name").getValue();
    }

    public Types.Direction getDirection() {
        return Types.Direction.fromString(properties.get("direction").getValue());
    }

    @Override
    public void accept(DependencyGraphVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString(){
        return "[" + getName() + ","+getDirection().toString()+","+getClass().getSimpleName()+"]";
    }
}
