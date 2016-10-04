package org.dama.datasynth.schnappi;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.dependencygraph.*;
import org.dama.datasynth.lang.dependencygraph.Literal;
import org.dama.datasynth.schnappi.ast.*;
import org.dama.datasynth.schnappi.ast.Number;
import org.dama.datasynth.schnappi.solver.Loader;
import org.dama.datasynth.schnappi.solver.Solver;

import java.util.*;

/**
 * Created by quim on 5/5/16.
 * The Chnappi compilers that creates a Schnappi program from a dependency graph
 */
public class Compiler extends DependencyGraphVisitor {

    private Map<String, List<Solver> >     solversDB           = new TreeMap<String,List<Solver>>();
    private Ast program             = new Ast();
    private Set<Long>             generatedVertices   = new HashSet<Long>();

    /**
     * Constructor
     * @param graph The dependency graph to build the Schnappi program from
     * @param dir The directory to locate the solvers
     */
    public Compiler(DependencyGraph graph, String dir){
        super(graph);
        loadSolvers(dir);
    }

    /**
     * Loads the solvers
     * @param dir The folder containing the solvers
     */
    private void loadSolvers(String dir){
        for(Solver s : Loader.loadSolvers(dir)) {
            String binding = s.getSignature().getBindings().values().iterator().next();
            if(!this.solversDB.containsKey(binding)) {
                List<Solver> solvers = new ArrayList<Solver>();
                solvers.add(s);
                this.solversDB.put(binding,solvers);

            } else {
                this.solversDB.get(binding).add(s);
            }
        }
    }


    /**
     * Synthetizes the Schnappi program
     */
    public void synthesizeProgram(){
        for(Entity v : graph.getEntities()) {
            visit(v);
        }

        for(Edge v : graph.getEdges()) {
            visit(v);
        }
    }

    /**
     * Solves a vertex
     * @param v The vertex to solve
     * @throws CompilerException
     */
    private void solveVertex(Vertex v) throws CompilerException {
        List<Solver> solvers = this.solversDB.get(v.getType());
        boolean found = false;
        if(solvers != null) {
            for (Solver solver : solvers) {
                if (solver.eval(graph, v)) {
                    this.concatenateProgram(solver.instantiate(graph, v));
                    found = true;
                }
            }
        }
        if (!found)
            throw new CompilerException(CompilerException.CompilerExceptionType.UNSOLVABLE_PROGRAM, "No solver for type " + v.getType());
    }

    /**
     * Concatenates an existing Ast to the current program ast
     * @param ast The ast to concatenate
     */
    private void concatenateProgram(Ast ast){
        List<org.dama.datasynth.schnappi.ast.Operation> statements = ast.getOperations();
        for(org.dama.datasynth.schnappi.ast.Operation statement : statements){
            this.program.addOperation(statement);
        }
    }

    /**
     * Gets the synthetized program Ast
     * @return The ast of the synthetized program
     */
    public Ast getProgram() {
        return this.program;
    }

    @Override
    public void visit(Entity entity) {
        if(!generatedVertices.contains(entity.getId())) {
            for(Vertex neighbor : graph.getNeighbors(entity)) {
                neighbor.accept(this);
            }
            generatedVertices.add(entity.getId());
            try {
                solveVertex(entity);
            } catch (CompilerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void visit(Attribute attribute) {
        if(!generatedVertices.contains(attribute.getId())) {
            for(Vertex neighbor : graph.getNeighbors(attribute)) {
                neighbor.accept(this);
            }
            if(!attribute.getName().contains(".oid")) {
                try {
                    solveVertex(attribute);
                } catch (CompilerException e) {
                    e.printStackTrace();
                }
            } else {
                Expression id = new Id(attribute.getName(), attribute.getIsTemporal());
                List<Expression> funcParams = new ArrayList<Expression>();
                funcParams.add(new Number(graph.getIncomingNeighbors(attribute,"internalAttribute").get(0).getProperties().get("number").toString(), Types.DataType.LONG));
                Expression right = new Function("range",funcParams);
                Assign assign = new Assign(id,right);
                program.addOperation(assign);
            }
            generatedVertices.add(attribute.getId());
        }
    }

    @Override
    public void visit(Edge edge) {
        if(!generatedVertices.contains(edge.getId())) {
            for(Vertex neighbor : graph.getNeighbors(edge)) {
                neighbor.accept(this);
            }
            try {
                solveVertex(edge);
            } catch (CompilerException e) {
                e.printStackTrace();
            }
            generatedVertices.add(edge.getId());
        }
    }

    @Override
    public void visit(Generator generator) {
        for(Vertex vertex : graph.getNeighbors(generator,"requires")) {
            vertex.accept(this);
        }
    }

    @Override
    public void visit(Literal literal) {
        throw new RuntimeException("Method visit Literal in compiler not implemented.");
    }
}
