package org.dama.datasynth.schnappi.ast;

import org.dama.datasynth.common.Types;

public class BinaryExpression extends Expression{
    private Expression left;
    private Expression right;
    private Types.LogicOperator operator;

    public BinaryExpression(Expression left, Expression right, Types.LogicOperator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public BinaryExpression(BinaryExpression binaryExpression) {
        this.left = binaryExpression.left.clone();
        this.right = binaryExpression.right.clone();
        this.operator = binaryExpression.getOperator();
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public Types.LogicOperator getOperator() {
        return operator;
    }

    @Override
    public boolean isAssignable() {
        return false;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
       return visitor.visit(this);
    }

    @Override
    public BinaryExpression clone() {
        return new BinaryExpression(this);
    }

    @Override
    public String toString() {
        return "<BinaryBindingExpression,"+operator.getText()+">";
    }
};
