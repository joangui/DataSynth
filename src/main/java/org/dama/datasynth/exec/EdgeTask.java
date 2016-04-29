package org.dama.datasynth.exec;

import java.util.ArrayList;

/**
 * Created by quim on 4/27/16.
 */
public class EdgeTask {
    public String entity1;
    public String entity2;
    public ArrayList<String> attributesEnt1;
    public ArrayList<String> attributesEnt2;

    public EdgeTask(String ent1, String ent2, ArrayList<String> attr1, ArrayList<String> attr2) {
        this.entity1 = ent1;
        this.entity2 = ent2;
        this.attributesEnt1 = attr1;
        this.attributesEnt2 = attr2;
    }

    public String getEntity1() {
        return entity1;
    }

    public void setEntity1(String entity1) {
        this.entity1 = entity1;
    }

    public String getEntity2() {
        return entity2;
    }

    public void setEntity2(String entity2) {
        this.entity2 = entity2;
    }

    public ArrayList<String> getAttributesEnt1() {
        return attributesEnt1;
    }

    public void setAttributesEnt1(ArrayList<String> attributesEnt1) {
        this.attributesEnt1 = attributesEnt1;
    }

    public ArrayList<String> getAttributesEnt2() {
        return attributesEnt2;
    }

    public void setAttributesEnt2(ArrayList<String> attributesEnt2) {
        this.attributesEnt2 = attributesEnt2;
    }
}
