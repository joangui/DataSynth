package org.dama.datasynth.program.schnappi;


import org.antlr.v4.runtime.tree.TerminalNode;
import org.dama.datasynth.program.Ast;
import org.dama.datasynth.program.schnappi.ast.*;
import org.dama.datasynth.program.schnappi.ast.Operation;
import org.dama.datasynth.program.solvers.Solver;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by quim on 5/17/16.
 */
public class SchnappiGeneratorVisitor extends org.dama.datasynth.program.schnappi.SchnappiParserBaseVisitor<Statement> {

    @Override
    public Solver visitSolver(org.dama.datasynth.program.schnappi.SchnappiParser.SolverContext ctx){


        Signature signature = visitSignature(ctx.signature());
        List<Binding> bindings = new LinkedList<Binding>();
        for(org.dama.datasynth.program.schnappi.SchnappiParser.BindContext bContext : ctx.bindings().bind() ) {
            Binding binding  = visitBind(bContext);
            bindings.add(binding);
        }

        Ast ast = new Ast();
        for(org.dama.datasynth.program.schnappi.SchnappiParser.OpContext op :  ctx.program().op()) {
           ast.addStatement(visitOp(op));

        }
        return new Solver(signature,bindings,ast);
    }

    @Override
    public Signature visitSignature(org.dama.datasynth.program.schnappi.SchnappiParser.SignatureContext ctx){
        return new Signature(ctx.source().VTYPE().getText(), ctx.target().VTYPE().getText());
    }

    @Override
    public Binding visitBind(org.dama.datasynth.program.schnappi.SchnappiParser.BindContext ctx){
        return new Binding( ctx.bindhead().getText(),ctx.ID().getSymbol().getText());
    }

    /*@Override
    public Statement visitProgram(org.dama.datasynth.program.schnappi.SchnappiParser.ProgramContext ctx){
        Statement n = new Statement("Program");
        for(org.dama.datasynth.program.schnappi.SchnappiParser.OpContext opc : ctx.op()) {
            n.addChild(visitOp(opc));
        }
        return n;
    }*/

    @Override
    public Operation visitOp(org.dama.datasynth.program.schnappi.SchnappiParser.OpContext ctx){
        if(ctx.assig() != null)  return visitAssig(ctx.assig());
        return null;
    }

    @Override
    public Function visitInit(org.dama.datasynth.program.schnappi.SchnappiParser.InitContext ctx){
        Parameters parameters = new Parameters();
        parameters.addParam(new Literal(ctx.ID().getSymbol().getText()));
        parameters.mergeParams(visitParams(ctx.params()));
        Function function= new Function("init", parameters);
        return function;
    }

    @Override
    public Assign visitAssig(org.dama.datasynth.program.schnappi.SchnappiParser.AssigContext ctx) {
        return new Assign(new Id(ctx.ID().getSymbol().getText()), visitExpr(ctx.expr()));
    }

    @Override
    public Expression visitExpr(org.dama.datasynth.program.schnappi.SchnappiParser.ExprContext ctx){
        if(ctx.funcs() != null) {
            return visitFuncs(ctx.funcs());
        } else if(ctx.NUM() != null) {
            return new Literal(ctx.NUM().getText());
        } else if(ctx.ID() != null) {
            return new Id(ctx.NUM().getText());
        }
        return null;
    }

    @Override
    public Expression visitFuncs(org.dama.datasynth.program.schnappi.SchnappiParser.FuncsContext ctx){
        if(ctx.init() != null) return visitInit(ctx.init());
        else if(ctx.genids() != null) return visitGenids(ctx.genids());
        else if(ctx.map() != null) return visitMap(ctx.map());
        else if(ctx.reduce() != null) return visitReduce(ctx.reduce());
        else if(ctx.union() != null) return visitUnion(ctx.union());
        else if(ctx.eqjoin() != null) return visitEqjoin(ctx.eqjoin());
        return null;
    }

    @Override
    public Function visitMap(org.dama.datasynth.program.schnappi.SchnappiParser.MapContext ctx) {
        Parameters parameters = new Parameters();
        for(TerminalNode tn : ctx.ID()) {
            parameters.addParam(new Id(tn.getSymbol().getText()));
        }
        return new Function("map",parameters);
    }

    @Override
    public Function visitUnion(org.dama.datasynth.program.schnappi.SchnappiParser.UnionContext ctx){
        return new Function("union",(visitParams(ctx.params())));
    }

    @Override
    public Function visitReduce(org.dama.datasynth.program.schnappi.SchnappiParser.ReduceContext ctx) {
        Parameters parameters = new Parameters();
        for(TerminalNode tn : ctx.ID()) {
            parameters .addParam(new Id(tn.getSymbol().getText()));
        }
        return new Function("reduce",parameters);
    }

    @Override
    public Function visitEqjoin(org.dama.datasynth.program.schnappi.SchnappiParser.EqjoinContext ctx){
        return new Function("eqjoin",visitParams(ctx.params()));
    }

    @Override
    public Parameters visitParams(org.dama.datasynth.program.schnappi.SchnappiParser.ParamsContext ctx){
        Parameters parameters = new Parameters();
        for(TerminalNode tn : ctx.ID()){
            parameters.addParam(new Id(tn.getSymbol().getText()));
        }
        return parameters;
    }

    @Override
    public Function visitGenids(org.dama.datasynth.program.schnappi.SchnappiParser.GenidsContext ctx){
        Parameters parameters = new Parameters();
        parameters.addParam(new Literal(ctx.NUM().getSymbol().getText()));
        return new Function("geinds",parameters);
    }

}