package org.dama.datasynth.program.schnappi;

import org.dama.datasynth.exec.*;
import org.dama.datasynth.program.Ast;
import org.dama.datasynth.program.solvers.Signature;
import org.dama.datasynth.program.solvers.Solver;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by quim on 5/5/16.
 */
public class Compiler {
    private Map<Signature, Solver> solversDB;
    private Ast program;
    public Compiler(){
        loadSolvers();
    }
    private void loadSolvers(){
        //
    }
    private void synthesizeProgram(DependencyGraph g){
        TopologicalOrderIterator<Vertex, DEdge> it = new TopologicalOrderIterator<>(g.getG());
        while(it.hasNext()){
            Vertex v = it.next();
            Set<DEdge> edges = g.getG().outgoingEdgesOf(v);
            for(DEdge e : edges){
                solveEdge(e);
            }
        }
    }
    private void solveEdge(DEdge e){
        //cool stuff happening here
        this.merge(solversDB.get(e.getSignature()).instantiate(e.getSource(), e.getTarget()));
        // this.program.appendSomeStuffSomePlace(
    }

    public Ast getProgram() {
        return program;
    }

    public void setProgram(Ast program) {
        this.program = program;
    }
    private void merge(Ast ast){
        //merge the new ast into this.program
    }
}
