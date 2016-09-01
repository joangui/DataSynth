package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.lang.Ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 4/27/16.
 */
public class Edge extends Vertex {

    private enum Direction {
        UNDIRECTED ("undirected"),
        DIRECTED ("directed");

        private String text = null;

        Direction(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    private Ast.Edge edge;
    private Entity entity;
    private Entity entity2;
    private ArrayList<Attribute> attributes;
    private ArrayList<Attribute> attributesEnt2;
    private Direction direction;

    public Edge(DependencyGraph graph, Ast.Edge edge, Entity ent1, ArrayList<Attribute> attr1 ) {
        super(graph, ":"+ent1.getEntity()+":");
        this.edge = edge;
        this.entity = ent1;
        this.entity2 = null;
        this.attributesEnt2 = null;
        this.direction = Direction.UNDIRECTED;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity1) {
        this.entity = entity1;
    }

    public Entity getEntity2() {
        return entity2;
    }

    public void setEntity2(Entity entity2) {
        this.entity2 = entity2;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributesEnt1) {
        this.attributes = attributesEnt1;
    }

    public ArrayList<Attribute> getAttributesEnt2() {
        return attributesEnt2;
    }

    public void setAttributesEnt2(ArrayList<Attribute> attributesEnt2) {
        this.attributesEnt2 = attributesEnt2;
    }

    public List<String> getInitParameters(){
        return new ArrayList<String>();
    }

    public List<String> getRunParameters(){
        return new ArrayList<String>();
    }

    public Entity getEntityByName(String name){
        if(name.equalsIgnoreCase(entity.getEntity())) return entity;
        else if(name.equalsIgnoreCase(entity2.getEntity())) return entity2;
        else return null;
    }

    public List<Attribute> getAttributesByName(String name){
        if(name.equalsIgnoreCase(entity.getEntity())) return attributes;
        else if(name.equalsIgnoreCase(entity2.getEntity())) return attributesEnt2;
        else return null;
    }

    public String getGenerator() {
        return edge.getGenerator();
    }

    @Override
    public void accept(DependencyGraphVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString(){
        return "[" + this.getId() + ","+direction.toString()+","+getClass().getSimpleName()+"]";
    }
}
