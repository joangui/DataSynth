package org.dama.datasynth.program.solvers;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.dama.datasynth.program.schnappi.SchnappiGeneratorVisitor;
import org.dama.datasynth.program.schnappi.SchnappiLexer;
import org.dama.datasynth.program.schnappi.SchnappiParser;

import java.io.IOException;

/**
 * Created by quim on 5/5/16.
 */
public class Parser {
    public static Solver parse( String file ){
        SchnappiLexer SchLexer = null;
        try {
            SchLexer = new SchnappiLexer( new ANTLRFileStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        CommonTokenStream tokens = new CommonTokenStream( SchLexer );
        SchnappiParser SchParser = new SchnappiParser( tokens );
        SchnappiParser.SolverContext sctx = SchParser.solver();
        SchnappiGeneratorVisitor visitor = new SchnappiGeneratorVisitor();
        return visitor.visitSolver(sctx);
    }
}
