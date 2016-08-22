package org.dama.datasynth.program.schnappi.ast.visitor;

import org.dama.datasynth.exec.ExecutableVertex;
import org.dama.datasynth.exec.Vertex;
import org.dama.datasynth.program.schnappi.ast.*;
import org.dama.datasynth.program.solvers.Solver;

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

    private String fetchValue(Id id, Vertex v){
        String aux = null;
        for(Binding binding : solver.getBindings()) {
            if(binding.getLhs().compareTo(id.getName())== 0) {
                aux = binding.getRhs();
                break;
            }
        }
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
                return null;
            }
        }
    }

    @Override
    public void visit(Assign n) {
        n.getId().accept(this);
        n.getExpression().accept(this);
    }

    @Override
    public void visit(Id n) {
        if(n.getName().substring(0,1).compareTo("@") == 0) {
            n.setName(fetchValue(n,vertex));
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

        ExecutableVertex at = (ExecutableVertex) vertex;
        if(n.getName().compareTo("init") == 0) {
            Parameters parameters = new Parameters();
            for(String str : at.getInitParameters()) {
                parameters.addParam(new Literal(str));
            }
            n.addParameters(parameters);
        }else {
            Parameters parameters = new Parameters();
            parameters.addParam(new Literal(String.valueOf(at.getRunParameters().size())));
            n.addParameters(parameters);
        }
        n.getParameters().accept(this);
    }

    @Override
    public void visit(Parameters n) {
        try {
            for (Expression param : n.getParams()) {
                param.accept(this);
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
}
