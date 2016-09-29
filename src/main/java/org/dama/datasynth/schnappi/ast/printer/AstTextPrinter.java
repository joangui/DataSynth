package org.dama.datasynth.schnappi.ast.printer;

import org.dama.datasynth.DataSynth;
import org.dama.datasynth.schnappi.ast.*;
import org.dama.datasynth.schnappi.ast.Number;
import org.dama.datasynth.schnappi.ast.Visitor;
import org.dama.datasynth.schnappi.solver.Solver;

import java.util.logging.Logger;

/**
 * Created by aprat on 22/08/16.
 */
public class AstTextPrinter extends Visitor<Node> {

    private static final Logger logger= Logger.getLogger( DataSynth.class.getSimpleName() );
}
