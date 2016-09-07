package org.dama.datasynth.schnappi.compilerpass;

import org.dama.datasynth.lang.dependencygraph.Vertex;
import org.dama.datasynth.schnappi.CompilerException;
import org.dama.datasynth.schnappi.ast.*;
import org.dama.datasynth.schnappi.ast.Number;
import org.dama.datasynth.schnappi.ast.Visitor;
import org.dama.datasynth.schnappi.solvers.Solver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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


    private Method findMethod(Vertex vertex, String methodName) {
        Method [] methods =  null;
        String className = "org.dama.datasynth.lang.dependencygraph."+vertex.getType();
        try {
            methods = vertex.getClass().asSubclass(Class.forName(className)).getMethods();
        } catch(ClassNotFoundException e) {
            throw new CompilerException("Error when processing binding. Unable to gind Class of type "+className+". Class not found.");
        }

        for(Method m : methods) {
            if(m.getParameterCount() == 0) {
                if (m.isAnnotationPresent(Vertex.Schnappi.class)) {
                    Vertex.Schnappi annotation = m.getAnnotation(org.dama.datasynth.lang.dependencygraph.Vertex.Schnappi.class);
                    if (annotation.name().compareTo(methodName) == 0) return m;
                }
            }
        }
        throw new CompilerException("Error when processing binding. Unable to find a method with name \""+methodName+"\" in vertex of type "+vertex.getType());
    }

    private List<Expression> processBinding(Binding binding) {
        List<Expression> retList = new LinkedList<Expression>();
        String value = binding.getValue();
        int pointIndex = value.indexOf('.');
        String methodName = value.substring(pointIndex+1,value.length());
        Method method = findMethod(vertex,methodName);
        try {
            Class returnType = method.getReturnType();
            if(String.class.isAssignableFrom(returnType)) {
                retList.add(new Any((String)method.invoke(vertex)));
            } else {
                if(Collection.class.isAssignableFrom(returnType)) {
                    Collection<String> strs = (Collection)method.invoke(vertex);
                    for(String str : strs) {
                        retList.add(new Any(str));
                    }
                } else {
                    throw new CompilerException("Method " + method.getName() + " in type " + vertex.getType() + " has an invalid return type "+returnType.getName());
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
            List<Expression> exprs = processBinding((Binding)n.getId());
            if(exprs.size() > 1) throw new CompilerException("Invalid binding replacement in assign operation. Cannot assign more than one expression.");
            if(exprs.size() == 0) throw new CompilerException("Invalid binding replacement in assign operation. Binding in assign operation must return one corresponding binging");
        //    if(exprs.get(0).getType().compareTo("Id") != 0) throw new CompilerException("Invalid binding replacement in assign operation. Cannot assign an expression to something different than an Id.");
            n.setId(new Id(((Any)exprs.get(0)).getValue()));
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
    }


    @Override
    public void visit(Expression n) {

    }

    @Override
    public void visit(Function n) {
        n.getParameters().accept(this);
    }

    @Override
    public void visit(Parameters n) {
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
