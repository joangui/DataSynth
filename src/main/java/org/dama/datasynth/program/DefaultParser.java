package org.dama.datasynth.program;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.dama.datasynth.program.schnappi.SchnappiGeneratorVisitor;
import org.dama.datasynth.program.schnappi.SchnappiLexer;
import org.dama.datasynth.program.schnappi.SchnappiParser;
import org.dama.datasynth.program.schnappi.ast.Node;
import org.dama.datasynth.program.solvers.DefaultSolver;
import org.dama.datasynth.program.solvers.Solver;

import java.io.IOException;

/**
 * Created by quim on 5/25/16.
 */
public class DefaultParser {
    private String file;
    private DefaultSolver solver;
    public DefaultParser(String file){
        this.file = file;
        this.solver = this.parse();
    }
    public Solver getSolver() {
        return this.solver;
    }
    private DefaultSolver parse(){
        SchnappiLexer SchLexer = null;
        try {
            SchLexer = new SchnappiLexer( new ANTLRFileStream(this.file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        CommonTokenStream tokens = new CommonTokenStream( SchLexer );
        SchnappiParser SchParser = new SchnappiParser( tokens );
        SchnappiParser.SolverContext sctx = SchParser.solver();
        SchnappiGeneratorVisitor visitor = new SchnappiGeneratorVisitor();
        Node n = visitor.visitSolver(sctx);
        /*Solver s = new Solver()
        return new Ast(n);*/
        return null;
    }
}
