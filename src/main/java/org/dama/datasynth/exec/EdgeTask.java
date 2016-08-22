package org.dama.datasynth.exec;

import org.dama.datasynth.lang.Ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 4/27/16.
 */
public class EdgeTask extends Vertex {
    public Ast.Edge edge;
    public EntityTask entity1;
    public EntityTask entity2;
    public ArrayList<AttributeTask> attributesEnt1;
    public ArrayList<AttributeTask> attributesEnt2;

    public EdgeTask(Ast.Edge edge, EntityTask ent1, EntityTask ent2, ArrayList<AttributeTask> attr1, ArrayList<AttributeTask> attr2) {
        super(ent1.getEntity()+"::"+ent2.getEntity(),"relation");
        this.edge = edge;
        this.entity1 = ent1;
        this.entity2 = ent2;
        this.attributesEnt1 = attr1;
        this.attributesEnt2 = attr2;
    }

    public EntityTask getEntity1() {
        return entity1;
    }

    public void setEntity1(EntityTask entity1) {
        this.entity1 = entity1;
    }

    public EntityTask getEntity2() {
        return entity2;
    }

    public void setEntity2(EntityTask entity2) {
        this.entity2 = entity2;
    }

    public ArrayList<AttributeTask> getAttributesEnt1() {
        return attributesEnt1;
    }

    public void setAttributesEnt1(ArrayList<AttributeTask> attributesEnt1) {
        this.attributesEnt1 = attributesEnt1;
    }

    public ArrayList<AttributeTask> getAttributesEnt2() {
        return attributesEnt2;
    }

    public void setAttributesEnt2(ArrayList<AttributeTask> attributesEnt2) {
        this.attributesEnt2 = attributesEnt2;
    }

    public List<String> getInitParameters(){
        return new ArrayList<String>();
    }

    public List<String> getRunParameters(){
        return new ArrayList<String>();
    }

    public EntityTask getEntityByName(String name){
        if(name.equalsIgnoreCase(entity1.getEntity())) return entity1;
        else if(name.equalsIgnoreCase(entity2.getEntity())) return entity2;
        else return null;
    }

    public List<AttributeTask> getAttributesByName(String name){
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
