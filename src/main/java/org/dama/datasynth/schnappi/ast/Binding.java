package org.dama.datasynth.schnappi.ast;

import org.dama.datasynth.common.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 5/18/16.
 * Represents a Binding in the Schnappi Ast
 */
public class Binding extends BindingExpression {

    public static class EdgeExpansion {
        private Types.Direction direction;
        private String name;

        public EdgeExpansion(Types.Direction direction, String name) {
            this.direction = direction;
            this.name = name;
        }

        public Types.Direction getDirection() {
            return direction;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "<"+direction.toString()+","+name+">";
        }
    }
    private List<EdgeExpansion> expansionChain = null;
    private String              leaf = null;
    private String              root = null;

    /**
     * Constructor
     */
    public Binding(String root, String leaf) {
        this.root = root;
        this.leaf = leaf;
        this.expansionChain = new ArrayList<EdgeExpansion>();
    }

    /**
     * Copy constructor
     * @param binding The binding to copy from
     */
    public Binding(Binding binding) {
        this.expansionChain = new ArrayList<EdgeExpansion>();
        this.expansionChain.addAll(binding.expansionChain);
        this.leaf = binding.leaf;
    }

    /**
     * Gets the binding chain of the binding
     * @return The binding chain of the binding
     */
    public List<EdgeExpansion> getExpansionChain() {
        return expansionChain;
    }


    /**
     * Gets the leaf of the binding
     * @return The leaf of the binding
     */
    public String getLeaf() {
        return leaf;
    }

    /**
     * Sets the leaf of this binding
     * @param leaf The leaf to set
     */
    public void setLeaf(String leaf) {
        this.leaf = leaf;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public void addExpansion(String name, Types.Direction direction) {
        expansionChain.add(new EdgeExpansion(direction,name));
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Binding clone() {
        return new Binding(this);
    }

    @Override
    public String toString() {
        return "<Binding,"+getRoot()+","+expansionChain.toString()+","+getLeaf()+">";
    }
}
