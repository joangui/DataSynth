package org.dama.datasynth.program.schnappi;

import org.dama.datasynth.exec.*;
import org.dama.datasynth.program.Ast;
import org.dama.datasynth.program.schnappi.ast.AtomNode;
import org.dama.datasynth.program.schnappi.ast.FuncNode;
import org.dama.datasynth.program.schnappi.ast.Node;
import org.dama.datasynth.program.schnappi.ast.ParamsNode;
import org.dama.datasynth.program.solvers.Loader;
import org.dama.datasynth.program.solvers.SignatureVertex;
import org.dama.datasynth.program.solvers.Solver;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by quim on 5/5/16.
 */
public class Compiler {

    private Map<SignatureVertex, Solver> solversDB;
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
        while(it.hasNext()) {
            Vertex v = it.next();
            if (v.getType().equalsIgnoreCase("attribute")) {
                if (!v.getId().equalsIgnoreCase("person.oid")) {
                    if (g.getG().incomingEdgesOf(v).size() > 0) addIncomingUnion(v, g);
                    try {
                        solveVertex(v);
                    } catch (CompileException e) {
                        e.printStackTrace();
                    }
                } else {
                    FuncNode n = new FuncNode("genids");
                    ParamsNode pn = new ParamsNode("params");
                    AttributeTask at = (AttributeTask) v;
                    pn.addParam(String.valueOf(at.getNumEntities()));
                    n.addChild(pn);
                    Node np = new Node("OP", "op");
                    Node na = new Node("ASSIG", "assig");
                    Node ne = new Node("EXPR", "expr");
                    ne.addChild(n);
                    na.addChild(new AtomNode(v.getId(), "ID"));
                    na.addChild(ne);
                    np.addChild(na);
                    this.program.getRoot().addChild(np);
                }
            }else if(v.getType().equalsIgnoreCase("entity")){
                Set<DEdge> edges = g.getG().incomingEdgesOf(v);
                Node np = new Node("OP", "op");
                Node na = new Node("ASSIG", "assig");
                Node ne = new Node("EXPR", "expr");
                FuncNode n = new FuncNode("union");
                ParamsNode pn = new ParamsNode("params");
                for(DEdge e : edges){
                    pn.addParam(e.getSource().getId());
                }
                n.addChild(pn);
                ne.addChild(n);
                na.addChild(new AtomNode(v.getId() + ".final", "ID"));
                na.addChild(ne);
                np.addChild(na);
                this.program.getRoot().addChild(np);
            }
        }
        System.out.print("\n");
    }
    private void solveVertex(Vertex v) throws CompileException {
        Solver s = this.solversDB.get(v.getSignature());
        if(s == null) throw new CompileException("Unsolvable program");
        this.concatenateProgram(s.instantiate(v));
    }
    private void solveEdge(DEdge e) throws CompileException {
        Solver s = this.solversDB.get(e.getSignature());
        if(s == null) throw new CompileException("Unsolvable program");
        //this.concatenateProgram(s.instantiate(e));
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
    private void addIncomingUnion(Vertex v, DependencyGraph g){
        Set<DEdge> edges = g.getG().incomingEdgesOf(v);
        Node np = new Node("OP", "op");
        Node na = new Node("ASSIG", "assig");
        Node ne = new Node("EXPR", "expr");
        FuncNode n = new FuncNode("union");
        ParamsNode pn = new ParamsNode("params");
        for(DEdge e : edges){
            pn.addParam(e.getSource().getId());
        }
        n.addChild(pn);
        ne.addChild(n);
        na.addChild(new AtomNode(v.getId() + ".input", "ID"));
        na.addChild(ne);
        np.addChild(na);
        this.program.getRoot().addChild(np);
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
