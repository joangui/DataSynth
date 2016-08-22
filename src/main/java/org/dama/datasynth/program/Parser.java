package org.dama.datasynth.program;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.dama.datasynth.program.schnappi.SchnappiGeneratorVisitor;
import org.dama.datasynth.program.schnappi.SchnappiLexer;
import org.dama.datasynth.program.schnappi.SchnappiParser;
import org.dama.datasynth.program.schnappi.ast.Statement;
import org.dama.datasynth.program.schnappi.ast.Signature;
import org.dama.datasynth.program.solvers.Solver;

import java.io.IOException;

/**
 * Created by quim on 5/5/16.
 */
public class Parser {

    private String file;
    private Solver solver;

    public Parser(String file){
        this.file = file;
        this.solver = this.parse();
    }

    public Solver getSolver() {
        return this.solver;
    }

    public Solver parse(){
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
        return visitor.visitSolver(sctx);
    }

    /*public Signature getSignature(Statement root){
        Signature sn = (Signature) root.getChild(0);
        String source = sn.getChild(0).id;
        String target = sn.getChild(1).id;
        return new Signature(source,target);
    }*/

    public Signature getSignature(Statement root){
        return new Signature(solver.getSignature());
    }
}
