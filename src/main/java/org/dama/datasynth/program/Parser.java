package org.dama.datasynth.program;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.dama.datasynth.program.schnappi.SchnappiGeneratorVisitor;
import org.dama.datasynth.program.schnappi.SchnappiLexer;
import org.dama.datasynth.program.schnappi.SchnappiParser;
import org.dama.datasynth.program.schnappi.ast.BindingNode;
import org.dama.datasynth.program.schnappi.ast.Node;
import org.dama.datasynth.program.schnappi.ast.SignatureNode;
import org.dama.datasynth.program.solvers.Signature;
import org.dama.datasynth.program.solvers.Solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
        Node n = visitor.visitSolver(sctx);
        Solver s = new Solver(new Ast(n));
        s.bindings = this.getBindings(n);
        s.signature = this.getSignature(n);
        return s;
    }
    public HashMap<String, String> getBindings(Node root){
        HashMap<String, String> binds = new HashMap<>();
        for(Node nn : root.getChild(1).children){
            BindingNode bn = (BindingNode) nn;
            binds.put(bn.lhs,bn.rhs);
            //System.out.println(bn.lhs + " => " + bn.rhs);
        }
        return binds;
    }
    public Signature getSignature(Node root){
        SignatureNode sn = (SignatureNode) root.getChild(0);
        String source = sn.getChild(0).id;
        String target = sn.getChild(1).id;
        return new Signature(source,target);
    }
}
