package org.dama.datasynth.program;

import org.dama.datasynth.utils.Reader;

/**
 * Created by quim on 5/5/16.
 */
public class Solver {
    private String rawRep;
    private Ast astRep;
    public Solver(String file) {
        this.rawRep = new Reader(file).text;
    }
    private void parse(String file){
        this.astRep = new Parser(file).getAst();
    }
}
