package org.dama.datasynth.program.schnappi.ast.visitor;

import org.dama.datasynth.lang.dependencygraph.ExecutableVertex;
import org.dama.datasynth.lang.dependencygraph.Vertex;
import org.dama.datasynth.program.schnappi.CompilerException;
import org.dama.datasynth.program.schnappi.ast.*;
import org.dama.datasynth.program.schnappi.ast.Number;
import org.dama.datasynth.program.solvers.Solver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by aprat on 22/08/16.
 */
public class SolverInstantiator implements Visitor {

    private Solver solver = null;
    private Vertex vertex = null;

    public SolverInstantiator(Solver solver, Vertex vertex) {
        this.solver = solver;
        this.vertex = vertex;
    }

    private String fetchValue(Binding id, Vertex v){
        String aux = "";
        switch(aux){
            case "@source.generator" : {
                ExecutableVertex at = (ExecutableVertex) v;
                return at.getGenerator();
            }
            case "@source.input" : {
                ExecutableVertex at = (ExecutableVertex) v;
                return at.getId()+".input";
            }
            case "@source.id" : {
                ExecutableVertex at = (ExecutableVertex) v;
                return at.getId();
            }
            default : {
                return aux;
            }
        }
    }

    private Method findMethod(Vertex vertex, String methodName) {
        Method [] methods = vertex.getClass().getMethods();
        for(Method m : methods) {
            if(m.getParameterCount() == 0) {
                if (m.isAnnotationPresent(Vertex.Schnappi.class)) {
                    Vertex.Schnappi annotation = m.getAnnotation(org.dama.datasynth.lang.dependencygraph.Vertex.Schnappi.class);
                    if (annotation.name().compareTo(methodName) == 0) return m;
                }
            }
        }
        return null;
    }

    private List<Expression> processBinding(Binding binding) {
        List<Expression> retList = new LinkedList<Expression>();
        String value = binding.getValue();
        int pointIndex = value.indexOf('.');
        String methodName = value.substring(pointIndex,value.length());
        Method method = findMethod(vertex,methodName);
        try {
            Class returnType = method.getReturnType().getClass();
            if(returnType.getSimpleName().compareTo("String") == 0) {
                retList.add(new Any((String)method.invoke(vertex)));
            } else {
                if(Collection.class.isAssignableFrom(returnType)) {
                    Collection<String> strs = (Collection)method.invoke(vertex);
                    for(String str : strs) {
                        retList.add(new Any(str));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new CompilerException("Method "+method.getName()+" in type "+vertex.getType()+" cannot be called for binding substitution purposes");
        } catch (InvocationTargetException e) {
            throw new CompilerException("Method "+method.getName()+" in type "+vertex.getType()+" cannot be called for binding substitution purposes");
        }
        return retList;
    }

    @Override
    public void visit(Assign n) {
        if(n.getId().getType().compareTo("Binding") != 0) {
            n.getId().accept(this);
        } else {
            List<Expression> exprs = processBinding((Binding)n.getExpression());
            if(exprs.size() > 1) throw new CompilerException("Invalid binding replacement in assign operation. Cannot assign more than one expression.");
            if(exprs.get(0).getType().compareTo("Id") != 0) throw new CompilerException("Invalid binding replacement in assign operation. Cannot assign an expression to something different than an Id.");
            n.setId((Id)exprs.get(0));
        }

        if(n.getExpression().getType().compareTo("Binding") != 0) {
            n.getExpression().accept(this);
        } else {
            List<Expression> exprs = processBinding((Binding)n.getExpression());
            if(exprs.size() > 1) throw new CompilerException("Invalid binding replacement in assign operation. Cannot assign more than one expression.");
            n.setExpression(exprs.get(0));
        }
    }

    @Override
    public void visit(Binding n) {
        n.setValue(fetchValue(n,vertex));
    }


    @Override
    public void visit(Expression n) {

    }

    @Override
    public void visit(Function n) {
        /*ExecutableVertex at = (ExecutableVertex) vertex;
        if(n.getName().compareTo("init") == 0) {
            Parameters parameters = new Parameters();
            for(String str : at.getInitParameters()) {
                parameters.addParam(new Literal(str));
            }
            n.addParameters(parameters);
        } else {
            Parameters parameters = new Parameters();
            parameters.addParam(new Literal(String.valueOf(at.getRunParameters().size())));
            n.addParameters(parameters);
        }*/
        n.getParameters().accept(this);
    }

    @Override
    public void visit(Parameters n) {
        try {
            for (Expression param : n.getParams()) {
                if(param.getType().compareTo("Binding") != 0) {
                    param.accept(this);
                } else {
                    Binding binding = (Binding) param;
                }
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    @Override
    public void visit(Signature n) {

    }

    @Override
    public void visit(Solver n) {

    }

    @Override
    public void visit(Operation n) {

    }

    @Override
    public void visit(Any any) {

    }

    @Override
    public void visit(Id n) {

    }

    @Override
    public void visit(StringLiteral n) {

    }

    @Override
    public void visit(Number n) {

    }
}
