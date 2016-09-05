package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;

/**
 * Created by quim on 4/27/16.
 */
public class Edge extends Vertex {


    private String name     = null;
    private Types.Direction direction;

    public Edge(String name, Types.Direction direction) {
        super();
        this.name = name;
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Types.Direction getDirection() {
        return direction;
    }

    public void setDirection(Types.Direction direction) {
        this.direction = direction;
    }

    @Override
    public void accept(DependencyGraphVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString(){
        return "[" + name + ","+direction.toString()+","+getClass().getSimpleName()+"]";
    }
}
