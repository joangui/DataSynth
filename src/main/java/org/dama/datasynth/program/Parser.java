package org.dama.datasynth.program;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.dama.datasynth.program.schnappi.SchnappiGeneratorVisitor;
import org.dama.datasynth.program.schnappi.SchnappiLexer;
import org.dama.datasynth.program.schnappi.SchnappiParser;
import org.dama.datasynth.program.schnappi.ast.Node;
import org.dama.datasynth.program.solvers.Solver;

import java.io.IOException;

/**
 * Created by quim on 5/5/16.
 */
public abstract class Parser {
    private String file;
    private Solver solver;
    public Parser(String file){
        this.file = file;
        this.solver = this.parse();
    }
    public Solver getSolver() {
        return this.solver;
    }
    protected abstract Solver parse();
}
