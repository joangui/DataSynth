package org.dama.datasynth.schnappi.compilerpass;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.dependencygraph.DependencyGraph;
import org.dama.datasynth.lang.dependencygraph.Vertex;
import org.dama.datasynth.schnappi.CompilerException;
import org.dama.datasynth.schnappi.ast.*;
import org.dama.datasynth.schnappi.ast.Number;
import org.dama.datasynth.schnappi.solver.DependencyGraphMatcher;
import org.dama.datasynth.schnappi.solver.Solver;

import java.util.*;

/**
 * Created by aprat on 22/08/16.
 * Schnappi Ast Visitor that to instantiate a solver
 */
public class SSAChecker extends Visitor {

    private Set<String> exists = new HashSet<String>();

    @Override
    public Node visit(Assign n) {
        /*if(exists.contains(n.getId().getValue())) {
            throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_VARIABLE_NAME, n.getId().getValue()+" already exists. Schnappi must be in SSA form.");
        }
        exists.add(n.getId().getValue());*/
        return null;
    }
}
