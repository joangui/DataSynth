package org.dama.datasynth.schnappi.compilerpass;

import org.dama.datasynth.schnappi.ast.Assign;
import org.dama.datasynth.schnappi.ast.Node;
import org.dama.datasynth.schnappi.ast.Visitor;

import java.util.HashSet;
import java.util.Set;

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
