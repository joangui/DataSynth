package org.dama.datasynth.program.ast;

import org.dama.datasynth.program.solvers.Loader;
import org.dama.datasynth.program.solvers.Parser;
import org.dama.datasynth.program.solvers.Solver;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 24/08/16.
 */
public class ShnappiParserTest {

    @Test
    public void schnappiParserTest(){
        try {
            ArrayList<Solver> solvers = Loader.loadSolvers("src/main/resources/solvers");
        } catch(Exception e) {
            assertTrue("Error when parsing solvers.",false);
        }
    }
}
