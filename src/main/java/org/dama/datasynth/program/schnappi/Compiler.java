package org.dama.datasynth.program.schnappi;

import org.dama.datasynth.exec.*;
import org.dama.datasynth.program.Ast;
import org.dama.datasynth.program.schnappi.ast.*;
import org.dama.datasynth.program.solvers.Loader;
import org.dama.datasynth.program.solvers.Solver;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;

/**
 * Created by quim on 5/5/16.
 */
public class Compiler extends DependencyGraphVisitor {

    private Map<String, Solver> solversDB;
    private Ast program;

    public Compiler(DependencyGraph graph, String dir){
        super(graph);
        loadSolvers(dir);
        this.program = new Ast();
    }

    private void loadSolvers(String dir){
        this.solversDB = new TreeMap<String,Solver>( new Comparator<String>() {
            public int compare( String a, String b) {
            return a.compareToIgnoreCase(b);
        }
        });
        for(Solver s : Loader.loadSolvers(dir)) {
            this.solversDB.put(s.getSignature().getSource(),s);
        }
    }

    public void synthesizeProgram(){
        TopologicalOrderIterator<Vertex, DEdge> it = new TopologicalOrderIterator<>(graph);
        while(it.hasNext()) {
            Vertex v = it.next();
            v.accept(this);
        }
        System.out.print("\n");
    }

    private void solveVertex(Vertex v) throws CompileException {
        Solver s = this.solversDB.get(v.getType());
        if(s == null) throw new CompileException("Unsolvable program. No solver for type "+v.getType());
        this.concatenateProgram(s.instantiate(v));
    }

    private void solveEdge(DEdge e) throws CompileException {
        /*Solver s = this.solversDB.get(e.getSignature());
        if(s == null) throw new CompileException("Unsolvable program");
        */
        //this.concatenateProgram(s.instantiate(e));
        //cool stuff happening here
        //this.merge(solversDB.get(e.getSignature()).instantiate(e.getSource(), e.getTarget()));
        // this.program.appendSomeStuffSomePlace(
    }

    private void concatenateProgram(Ast p){
        List<Operation> statements = p.getStatements();
        for(Operation statement : statements){
            this.program.addStatement(statement);
        }
    }

    private void addIncomingUnion(Vertex v, DependencyGraph g, String suffix){
        Set<DEdge> edges = g.incomingEdgesOf(v);

        Parameters parameters = new Parameters();
        for(DEdge e : edges){
            parameters.addParam(new Id(e.getSource().getId()));
        }
        Function function = new Function("union",parameters);
        Assign assign = new Assign(new Id(v.getId() + suffix),  function);
        this.program.addStatement(assign);
    }

    private void addIncomingCartesianProduct(Vertex v, DependencyGraph g, String suffix){
        Set<DEdge> edges = g.incomingEdgesOf(v);
        Parameters parameters = new Parameters();
        long index = 0;
        for(DEdge e : edges){
            parameters.addParam( new Id(e.getSource().getId()+".filtered["+index+"]"));
            ++index;
        }
        Function function = new Function("cartesian", parameters);
        Assign assign = new Assign(new Id(v.getId() + suffix),function);
        this.program.addStatement(assign);
    }

    private void addFilters(Edge v, DependencyGraph g){
        Set<DEdge> edges = g.incomingEdgesOf(v);
        long index = 0;
        for(DEdge e : edges){
            addFilter(v, v.getAttributesByName(e.getSource().getId()), e.getSource().getId(), index);
            ++index;
        }
    }

    private void addFilter(Edge v, List<Attribute> attrs, String entityName, long ind){
        Parameters parameters = new Parameters();
        for(Attribute attr : attrs){
            parameters.addParam(new Id(attr.getId()));
        }
        Function function = new Function("filter",parameters);
        Assign assign = new Assign(new Id(entityName + ".filtered["+ind+"]"),function);
        this.program.addStatement(assign);
    }

    public Ast getProgram() {
        return this.program;
    }

    public void setProgram(Ast program) {
        this.program = program;
    }

    private void merge(Solver solver){
        program.merge(solver.getOperations());
    }

    @Override
    public void visit(Entity entity) {
        addIncomingUnion(entity, graph, ".final");
    }

    @Override
    public void visit(Attribute attribute) {
        if (!attribute.getId().equalsIgnoreCase("person.oid")) {
            if (graph.incomingEdgesOf(attribute).size() > 0) addIncomingUnion(attribute, graph, ".input");
            try {
                solveVertex(attribute);
            } catch (CompileException e) {
                e.printStackTrace();
            }
        } else {
            Parameters parameters = new Parameters( new Literal(String.valueOf(attribute.getEntity().getNumInstances())));
            Function function = new Function("genids",parameters);
            Assign assign = new Assign(new Id(attribute.getId()),function);
            this.program.addStatement(assign);
        }
    }

    @Override
    public void visit(Edge edge) {

        addFilters(edge, graph);
        addIncomingCartesianProduct( edge, graph,".input");
        try {
            solveVertex(edge);
        } catch (CompileException e) {
            e.printStackTrace();
        }

    }
}
