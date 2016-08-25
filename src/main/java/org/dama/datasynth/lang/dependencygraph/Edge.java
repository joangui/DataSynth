package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.lang.Ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 4/27/16.
 */
public class Edge extends Vertex {

    public Ast.Edge edge;
    public Entity entity1;
    public Entity entity2;
    public ArrayList<Attribute> attributesEnt1;
    public ArrayList<Attribute> attributesEnt2;

    public Edge(DependencyGraph graph, Ast.Edge edge, Entity ent1, Entity ent2, ArrayList<Attribute> attr1, ArrayList<Attribute> attr2) {
        super(graph, ent1.getEntity()+"::"+ent2.getEntity());
        this.edge = edge;
        this.entity1 = ent1;
        this.entity2 = ent2;
        this.attributesEnt1 = attr1;
        this.attributesEnt2 = attr2;
    }

    public Entity getEntity1() {
        return entity1;
    }

    public void setEntity1(Entity entity1) {
        this.entity1 = entity1;
    }

    public Entity getEntity2() {
        return entity2;
    }

    public void setEntity2(Entity entity2) {
        this.entity2 = entity2;
    }

    public ArrayList<Attribute> getAttributesEnt1() {
        return attributesEnt1;
    }

    public void setAttributesEnt1(ArrayList<Attribute> attributesEnt1) {
        this.attributesEnt1 = attributesEnt1;
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
        if(name.equalsIgnoreCase(entity1.getEntity())) return entity1;
        else if(name.equalsIgnoreCase(entity2.getEntity())) return entity2;
        else return null;
    }

    public List<Attribute> getAttributesByName(String name){
        if(name.equalsIgnoreCase(entity1.getEntity())) return attributesEnt1;
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
}
