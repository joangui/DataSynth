package org.dama.datasynth.program.solvers;

import org.dama.datasynth.exec.Vertex;
import org.dama.datasynth.program.Ast;
import org.dama.datasynth.program.Parser;
import org.dama.datasynth.utils.Reader;

/**
 * Created by quim on 5/5/16.
 */
public abstract class Solver {
    private String rawRep;
    private Ast ast;
    private Signature signature;
    public Solver(String file) {
        this.rawRep = new Reader(file).text;
    }
    private void parse(String file){
        //this.ast = new Parser(file).getAst();
    }
    public Signature getSignature(){
        return this.signature;
    }

    public Ast getAst() {
        return ast;
    }

    public void setAst(Ast ast) {
        this.ast = ast;
    }
    public abstract Ast instantiate(Vertex s, Vertex t);
}
