package org.dama.datasynth.schnappi.ast;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.dependencygraph.DependencyGraph;
import org.dama.datasynth.lang.dependencygraph.Vertex;
import org.dama.datasynth.schnappi.CompilerException;
import org.dama.datasynth.schnappi.solver.DependencyGraphMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quim on 5/25/16.
 * Represents a solver signature in the Schnappi Ast
 */
public class Signature extends Node {

    public enum LogicOperator {
        EQ("=="),
        NEQ("!=");

        String text = null;
        LogicOperator(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public static LogicOperator fromString(String text)  {
            if (text != null) {
                for (LogicOperator b : LogicOperator.values()) {
                    if (text.equalsIgnoreCase(b.getText())) {
                        return b;
                    }
                }
            }
            return null;
        }
    }

    private class Operation {
        private Expression left;
        private Expression right;
        private LogicOperator operator;

        public Operation(Expression left, Expression right, LogicOperator operator) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }
    };

    private Map<String,String>  bindings = new HashMap<String,String>();
    private List<Operation>     operations = new ArrayList<Operation>();

    /**
     * Constructor
     */
    public Signature(){
    }

    /**
     * Copy constructor
     * @param signature The signature to copy from
     */
    public Signature(Signature signature) {
        bindings = new HashMap<String,String>(signature.bindings);
        operations = new ArrayList<Operation>(signature.operations);
    }

    /**
     * Adds a signature operation to the signature
     * @param left The left part of the comparison operation
     * @param right The right part of the comparison operation
     * @param operator The operator
     */
    public void addOperation(Expression left, Expression right, LogicOperator operator) {
        operations.add(new Operation(left, right, operator));
    }

    /**
     * Adds a binding to the signature
     * @param name The name of the binding
     * @param type The type of the binding
     */
    public void addBinding(String name, String type) {
        bindings.put(name,type);
    }

    /**
     * Gets the bindings of the signature
     * @return The bindings of the signature
     */
    public Map<String, String> getBindings() {
        return bindings;
    }


    /**
     * Decodes an atomic
     * @param graph The graph to use to decode the atomic
     * @param v The vertex the atomic should be matched against in case it is a binding
     * @param expression The expression to decode
     * @return The property value after decoding the atomic
     */
    private Object decodeExpression(DependencyGraph graph, Vertex v, Expression expression)  {
       switch(expression.getType()) {
           case "BindingFunction":
               return decodeBindingFunction(graph,v,(BindingFunction)expression);
           case "Binding":
               List<Object> values = DependencyGraphMatcher.match(graph,v,((Binding)expression));
               if(values.size() != 1) throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_EXPRESSION,". Only univalued expressions allowed in signature");
               return values.get(0);
           case "Number":
               return Long.parseLong(((Number)expression).getValue());
           case "StringLiteral":
               return ((StringLiteral)expression).getValue();
       }
       throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_EXPRESSION, ". Unsupported type "+expression.getType());
    }

    private Object decodeBindingFunction(DependencyGraph graph, Vertex v, BindingFunction function) {

        switch(function.getName()) {
            case "length":
                List<Object> values = DependencyGraphMatcher.match(graph,v,((Binding)function.getExpression()));
                return new Long(values.size());
            default:
                throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_EXPRESSION, ". Unsupported binding function "+function.getName());
        }

    }

    /**
     * Compares two property values
     * @param left The left property value in the comparison
     * @param right The right property value in the comparison
     * @param operator The operator of the comparison
     * @return The result of the comparison
     */
    private boolean comparePropertyValues(Object left, Object right, LogicOperator operator) {
        switch(operator) {
            case EQ:
                return Types.compare(left,right);
            case NEQ:
                return !Types.compare(left,right);
        }
        throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_EXPRESSION, ". Unsupported logic operator "+operator.getText());
    }

    /**
     * Evaluates a vertex against this signature
     * @param graph The dependency graph used for the evaluation
     * @param v The vertex to evaluate
     * @return True if the signature holds for the vertex
     */
    public boolean eval(DependencyGraph graph, Vertex v) {
        for(Operation operation : operations) {
            if(!comparePropertyValues(decodeExpression(graph,v,operation.left), decodeExpression(graph,v,operation.right), operation.operator)){
                return false;
            }
        }
        return true;
    }


    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Node clone() {
        return new Signature(this);
    }
}
