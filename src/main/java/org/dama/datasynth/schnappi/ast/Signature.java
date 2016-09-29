package org.dama.datasynth.schnappi.ast;

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

    public static enum LogicOperator {
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
        private Atomic left;
        private Atomic right;
        private LogicOperator operator;

        public Operation(Atomic left, Atomic right, LogicOperator operator) {
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
    public void addOperation(Atomic left, Atomic right, LogicOperator operator) {
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
     * @param atomic The atomic to decode
     * @return The property value after decoding the atomic
     */
    private Vertex.PropertyValue decodeAtomic(DependencyGraph graph, Vertex v, Atomic atomic)  {
       switch(atomic.getType()) {
           case "Binding":
                List<Vertex.PropertyValue> values = DependencyGraphMatcher.match(graph,v,((Binding)atomic).getBindingChain());
               if(values.size() != 1) throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_EXPRESSION,". Only univalued expressions allowed in signature");
               return values.get(0);
           case "Number":
               return new Vertex.PropertyValue(Long.parseLong((atomic.getValue())));
           case "StringLiteral":
               return new Vertex.PropertyValue(atomic.getValue());
       }
       throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_EXPRESSION, ". Unsupported type "+atomic.getType());

    }

    /**
     * Compares two property values
     * @param left The left property value in the comparison
     * @param right The right property value in the comparison
     * @param operator The operator of the comparison
     * @return The result of the comparison
     */
    private boolean comparePropertyValues(Vertex.PropertyValue left, Vertex.PropertyValue right, LogicOperator operator) {
        switch(operator) {
            case EQ:
                if(left.getDataType() != right.getDataType()) return false;
                return left.getValue().compareTo(right.getValue()) == 0;
            case NEQ:
                if(left.getDataType() != right.getDataType()) return true;
                return left.getValue().compareTo(right.getValue()) != 0;
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
                if(!comparePropertyValues(decodeAtomic(graph,v,operation.left), decodeAtomic(graph,v,operation.right), operation.operator)){
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
    public Node copy() {
        return new Signature(this);
    }
}
