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

    public Signature(){
    }

    public Signature(Signature signature) {
        bindings = new HashMap<String,String>(signature.bindings);
        operations = new ArrayList<Operation>(signature.operations);
    }

    public void addOperation(Atomic left, Atomic right, LogicOperator operator) {
        operations.add(new Operation(left, right, operator));
    }

    public void addBinding(String name, String type) {
        bindings.put(name,type);
    }

    public Map<String, String> getBindings() {
        return bindings;
    }


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

    public boolean eval(DependencyGraph graph, Vertex v) {
        for(Operation operation : operations) {
                if(!comparePropertyValues(decodeAtomic(graph,v,operation.left), decodeAtomic(graph,v,operation.right), operation.operator)){
                    return false;
                }
           }
       return true;
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Node copy() {
        return new Signature(this);
    }
}
