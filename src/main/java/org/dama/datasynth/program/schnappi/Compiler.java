package org.dama.datasynth.program.schnappi;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.dama.datasynth.exec.*;
import org.dama.datasynth.program.Ast;
import org.dama.datasynth.program.schnappi.ast.AtomNode;
import org.dama.datasynth.program.schnappi.ast.FuncNode;
import org.dama.datasynth.program.schnappi.ast.Node;
import org.dama.datasynth.program.schnappi.ast.ParamsNode;
import org.dama.datasynth.program.solvers.Loader;
import org.dama.datasynth.program.solvers.Signature;
import org.dama.datasynth.program.solvers.SignatureVertex;
import org.dama.datasynth.program.solvers.Solver;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;

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
            //System.out.print(" >> " + v.getId() + " :: " + v.getType());
            if (v.getType().equalsIgnoreCase("attribute")) {
                if (!v.getId().equalsIgnoreCase("person.oid")) {
                    if (g.getG().incomingEdgesOf(v).size() > 0) addIncomingUnion(v, g, ".input");
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
                addIncomingUnion(v, g, ".final");
            }else if(v.getType().equalsIgnoreCase("relation")){
                addFilters((EdgeTask) v,g);
                addIncomingCartesianProduct(v,g,".input");
                try {
                    solveVertex(v);
                } catch (CompileException e) {
                    e.printStackTrace();
                }
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
    private void addIncomingUnion(Vertex v, DependencyGraph g, String suffix){
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
        na.addChild(new AtomNode(v.getId() + suffix, "ID"));
        na.addChild(ne);
        np.addChild(na);
        this.program.getRoot().addChild(np);
    }
    private void addIncomingCartesianProduct(Vertex v, DependencyGraph g, String suffix){
        Set<DEdge> edges = g.getG().incomingEdgesOf(v);
        Node np = new Node("OP", "op");
        Node na = new Node("ASSIG", "assig");
        Node ne = new Node("EXPR", "expr");
        FuncNode n = new FuncNode("cartesian");
        ParamsNode pn = new ParamsNode("params");
        long index = 0;
        for(DEdge e : edges){
            pn.addParam(e.getSource().getId()+".filtered["+index+"]");
            ++index;
        }
        n.addChild(pn);
        ne.addChild(n);
        na.addChild(new AtomNode(v.getId() + suffix, "ID"));
        na.addChild(ne);
        np.addChild(na);
        this.program.getRoot().addChild(np);
    }
    private void addFilters(EdgeTask v, DependencyGraph g){
        Set<DEdge> edges = g.getG().incomingEdgesOf(v);
        long index = 0;
        for(DEdge e : edges){
            addFilter(v, v.getAttributesByName(e.getSource().getId()), e.getSource().getId(), index);
            ++index;
        }
    }
    private void addFilter(EdgeTask v, List<AttributeTask> attrs, String entityName, long ind){
        Node np = new Node("OP", "op");
        Node na = new Node("ASSIG", "assig");
        Node ne = new Node("EXPR", "expr");
        FuncNode n = new FuncNode("filter");
        ParamsNode pn = new ParamsNode("params");
        for(AttributeTask attr : attrs){
            pn.addParam(attr.getId());
        }
        n.addChild(pn);
        ne.addChild(n);
        na.addChild(new AtomNode(entityName + ".filtered["+ind+"]", "ID"));
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
