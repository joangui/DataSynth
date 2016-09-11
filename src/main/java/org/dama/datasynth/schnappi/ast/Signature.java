package org.dama.datasynth.schnappi.ast;

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

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Node copy() {
        return new Signature(this);
    }
}
