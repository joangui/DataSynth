package org.dama.datasynth.program.ast;

import org.dama.datasynth.program.solvers.Loader;
import org.dama.datasynth.program.solvers.Parser;
import org.dama.datasynth.program.solvers.Solver;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by aprat on 24/08/16.
 */
public class ShnappiParserTest {

    @Test
    public void schnappiParserTest(){
        ArrayList<Solver> solvers = Loader.loadSolvers("src/main/resources/solvers");
    }
}
