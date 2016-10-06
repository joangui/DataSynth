package org.dama.datasynth.schnappi.ast;

import org.dama.datasynth.common.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 5/18/16.
 * Represents a Binding in the Schnappi Ast
 */
public class EdgeExpansion extends Node{
    private Types.Direction direction;
    private String name;
    private List<BinaryExpression> filters = new ArrayList<BinaryExpression>();

    public EdgeExpansion(Types.Direction direction, String name) {
        this.direction = direction;
        this.name = name;
    }

    public EdgeExpansion(EdgeExpansion expansion) {
        this.direction = expansion.getDirection();
        this.name = expansion.getName();
        for (BinaryExpression expr : expansion.getFilters()) {
            filters.add(expr.clone());
        }
    }

    public Types.Direction getDirection() {
        return direction;
    }

    public String getName() {
        return name;
    }

    public void addFilter(BinaryExpression expr) {
        filters.add(expr);
    }

    public List<BinaryExpression> getFilters() {
        return filters;
    }

    @Override
    public String toString() {
        return "<"+direction.toString()+","+name+">";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new EdgeExpansion(this);
    }
}
