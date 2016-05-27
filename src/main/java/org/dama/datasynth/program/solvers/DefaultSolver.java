package org.dama.datasynth.program.solvers;

import org.dama.datasynth.exec.Vertex;
import org.dama.datasynth.program.Ast;

/**
 * Created by quim on 5/24/16.
 */
public class DefaultSolver extends Solver{
    @Override
    public Ast instantiate(Vertex s, Vertex t) {
        return null;
    }
    public DefaultSolver(String file){
        super(file);
    }
}
