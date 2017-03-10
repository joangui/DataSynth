package org.dama.datasynth.schnappi.compilerpass;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.dependencygraph.DependencyGraph;
import org.dama.datasynth.lang.dependencygraph.Vertex;
import org.dama.datasynth.schnappi.CompilerException;
import org.dama.datasynth.schnappi.ast.*;
import org.dama.datasynth.schnappi.ast.Number;
import org.dama.datasynth.schnappi.solver.DependencyGraphMatcher;
import org.dama.datasynth.schnappi.solver.Solver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprat on 22/08/16.
 * Schnappi Ast Visitor that to instantiate a solver
 */
public class SolverInstantiator extends Visitor<List<Expression>> {

    private Solver solver = null;
    private Vertex vertex = null;
    private DependencyGraph graph = null;

    public SolverInstantiator(DependencyGraph graph, Solver solver, Vertex vertex) {
        this.graph = graph;
        this.solver = solver;
        this.vertex = vertex;
    }


    private List<Expression> createExpressionList(Expression expression) {
        List<Expression> exprs = new ArrayList<Expression>();
        exprs.add(expression);
        return exprs;
    }

    @Override
    public void call(Ast ast) {
        super.call(ast);
    }

    @Override
    public List<Expression> visit(Assign n) {
        List<Expression> exprs = n.getId().accept(this);
        if(exprs.size() > 1) throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_ASSIGN, "Cannot assign more than one expression.");
        if(exprs.size() == 0) throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_ASSIGN, "Binding in assign operation must return one corresponding binging");
        if(!exprs.get(0).isAssignable())
                throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_ASSIGN, "Binding is not returning an assigneble expression");
        n.setId(exprs.get(0));

        exprs = n.getExpression().accept(this);
        if(exprs.size() > 1) throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_ASSIGN, "Cannot assign more than one expression.");
        if(exprs.size() == 0) throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_ASSIGN, "Binding returns zero elements in assignment.");
        n.setExpression(exprs.get(0));
        return null;
    }

    @Override
    public List<Expression> visit(Function n) {
        List<Expression> parameters = new ArrayList<Expression>();
        for(Expression expr: n.getParameters()) {
            parameters.addAll(expr.accept(this));
        }
        n.setParameters(parameters);
        List<Expression> retExpr = new ArrayList<Expression>();
        retExpr.add(n);
        return retExpr;
    }

    @Override
    public List<Expression> visit(Atomic atomic) {
        return createExpressionList(atomic);
    }

    @Override
    public List<Expression> visit(Var var) {
        return createExpressionList(var);
    }


    @Override
    public List<Expression> visit(StringLiteral literal) {
        return createExpressionList(literal);
    }

    @Override
    public List<Expression> visit(Number number) {
        return createExpressionList(number);
    }

    @Override
    public List<Expression> visit(Binding binding) {
        List<Object> values = DependencyGraphMatcher.match(graph,vertex,binding);
        List<Expression> retList = new ArrayList<Expression>();
        for(Object value : values) {
            if(Types.DataType.fromObject(value) == Types.DataType.LONG) {
                retList.add(new org.dama.datasynth.schnappi.ast.Number(value.toString(), Types.DataType.LONG));
                continue;
            }
            if(Types.DataType.fromObject(value) == Types.DataType.DOUBLE) {
                retList.add(new org.dama.datasynth.schnappi.ast.Number(value.toString(), Types.DataType.DOUBLE));
                continue;
            }
            if(Types.DataType.fromObject(value) == Types.DataType.ID) {
                retList.add(new Id(value.toString(),((Types.Id)value).isTemporal()));
                continue;
            }
            if(Types.DataType.fromObject(value) == Types.DataType.STRING) {
                retList.add(new StringLiteral(value.toString()));
                continue;
            }
        }
        return retList;
    }

}
