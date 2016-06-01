package org.dama.datasynth.program.schnappi;

import org.dama.datasynth.exec.*;
import org.dama.datasynth.program.Ast;
import org.dama.datasynth.program.schnappi.ast.Node;
import org.dama.datasynth.program.solvers.Loader;
import org.dama.datasynth.program.solvers.Signature;
import org.dama.datasynth.program.solvers.Solver;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by quim on 5/5/16.
 */
public class Compiler {
    private Map<Signature, Solver> solversDB;
    private Ast program;
    public Compiler(String dir){
        loadSolvers(dir);
        this.program = new Ast(new Node("main", "program"));
    }
    private void loadSolvers(String dir){
        this.solversDB = new HashMap<>();
        for(Solver s : Loader.loadSolvers(dir)) {
            this.solversDB.put(s.getSignature(),s);
        }
    }
    public void synthesizeProgram(DependencyGraph g){
        TopologicalOrderIterator<Vertex, DEdge> it = new TopologicalOrderIterator<>(g.getG());
        while(it.hasNext()){
            Vertex v = it.next();
            Set<DEdge> edges = g.getG().outgoingEdgesOf(v);
            for(DEdge e : edges){
                try {
                    solveEdge(e);
                } catch (CompileException e1) {
                    System.out.println();
                    for(Solver s : solversDB.values()){
                        Solver sp = this.solversDB.get(e.getSignature());
                        System.out.println("Edge " + e.getSignature() + " | Solver "+s.getSignature() + " | equal? "+ e.getSignature().equals(s.getSignature()) + " null? " + (sp == null));
                    }
                    //e1.printStackTrace();
                }
            }
        }
    }
    private void solveEdge(DEdge e) throws CompileException {
        Solver s = this.solversDB.get(e.getSignature());
        if(s == null) throw new CompileException("Unsolvable program");
        this.concatenateProgram(s.instantiate());
        //cool stuff happening here
        //this.merge(solversDB.get(e.getSignature()).instantiate(e.getSource(), e.getTarget()));
        // this.program.appendSomeStuffSomePlace(
    }
    private void concatenateProgram(Ast p){
        Node np = p.getRoot().getChild(2);
        for(Node nn : np.children){
            this.program.getRoot().addChild(nn);
        }
    }
    public Ast getProgram() {
        return this.program;
    }

    public void setProgram(Ast program) {
        this.program = program;
    }
    private void merge(Solver solver){
        Node r = this.program.getRoot();
        r.addChild(solver.getAst().getRoot());
    }
}
