package org.dama.datasynth.schnappi.compilerpass;

import org.apache.ivy.ant.IvyMakePom;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.dependencygraph.DependencyGraph;
import org.dama.datasynth.lang.dependencygraph.Vertex;
import org.dama.datasynth.schnappi.CompilerException;
import org.dama.datasynth.schnappi.ast.*;
import org.dama.datasynth.schnappi.ast.Number;
import org.dama.datasynth.schnappi.ast.Visitor;
import org.dama.datasynth.schnappi.solver.DependencyGraphMatcher;
import org.dama.datasynth.schnappi.solver.Solver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aprat on 22/08/16.
 * Schnappi Ast Visitor that to instantiate a solver
 */
public class SolverInstantiator extends Visitor {

    private Solver solver = null;
    private Vertex vertex = null;
    private DependencyGraph graph = null;

    public SolverInstantiator(DependencyGraph graph, Solver solver, Vertex vertex) {
        this.graph = graph;
        this.solver = solver;
        this.vertex = vertex;
    }


    /**
     * Processes a binding
     * @param binding The binding to process
     * @return The list of expressions resulting from this binding
     */
    private List<Expression> processBinding(Binding binding) {
        List<Vertex.PropertyValue> values = DependencyGraphMatcher.match(graph,vertex,binding.getBindingChain());
        List<Expression> retList = new ArrayList<Expression>();
        for(Vertex.PropertyValue value : values) {
                if(value.getDataType() == Types.DataType.LONG) {
                    retList.add(new org.dama.datasynth.schnappi.ast.Number(value.getValue(), Types.DataType.LONG));
                    continue;
                }
                if(value.getDataType() == Types.DataType.DOUBLE) {
                    retList.add(new org.dama.datasynth.schnappi.ast.Number(value.getValue(), Types.DataType.DOUBLE));
                    continue;
                }
            if(value.getDataType() == Types.DataType.ID) {
                    retList.add(new Id(value.getValue(),((Types.Id)value.getObject()).isTemporal()));
                    continue;
                }
            if(value.getDataType() == Types.DataType.STRING) {
                retList.add(new StringLiteral(value.getValue()));
                continue;
            }
        }
        return retList;
    }


    @Override
    public Node visit(Assign n) {
        if(n.getId().getType().compareTo("Binding") != 0) {
            n.getId().accept(this);
        } else {
            List<Expression> exprs = processBinding((Binding)n.getId());
            if(exprs.size() > 1) throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_ASSIGN, "Cannot assign more than one expression.");
            if(exprs.size() == 0) throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_ASSIGN, "Binding in assign operation must return one corresponding binging");
            if(exprs.get(0).getType().compareTo("Id") != 0) throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_ASSIGN, "Cannot return a variable name out of a binding for the left part in an assignment");
            n.setId((Id)exprs.get(0));
        }

        if(n.getExpression().getType().compareTo("Binding") != 0) {
            n.getExpression().accept(this);
        } else {
            List<Expression> exprs = processBinding((Binding)n.getExpression());
            if(exprs.size() > 1) throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_ASSIGN, "Cannot assign more than one expression.");
            n.setExpression(exprs.get(0));
        }
        return null;
    }


    @Override
    public Node visit(Function n) {
        n.getParameters().accept(this);
        return null;
    }

    @Override
    public Node visit(Parameters n) {
        try {
            ListIterator<Expression> iterator = n.getParams().listIterator();
            while(iterator.hasNext()) {
                Expression currentExpr = iterator.next();
                if(currentExpr.getType().compareTo("Binding") == 0) {
                    List<Expression> bindings = processBinding((Binding)currentExpr);
                    iterator.remove();
                    for(Expression binding : bindings) {
                        iterator.add(binding);
                    }
                }
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        return null;
    }

    @Override
    public Node visit(Atomic atomic) {
        return null;
    }

    @Override
    public Node visit(Var var) {
        return null;
    }

}
