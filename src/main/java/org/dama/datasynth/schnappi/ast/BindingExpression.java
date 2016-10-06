package org.dama.datasynth.schnappi.ast;

import org.dama.datasynth.lang.dependencygraph.DependencyGraph;
import org.dama.datasynth.lang.dependencygraph.Vertex;
import org.dama.datasynth.schnappi.CompilerException;
import org.dama.datasynth.schnappi.solver.DependencyGraphMatcher;

import java.util.List;

/**
 * Created by quim on 5/18/16.
 * Represents a Binding in the Schnappi Ast
 */
public abstract class BindingExpression extends Expression {

    @Override
    public abstract BindingExpression clone();

}
