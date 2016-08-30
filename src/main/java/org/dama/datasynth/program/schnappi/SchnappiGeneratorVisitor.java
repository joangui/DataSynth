package org.dama.datasynth.program.schnappi;


import org.antlr.v4.runtime.tree.TerminalNode;
import org.dama.datasynth.program.Ast;
import org.dama.datasynth.program.schnappi.ast.*;
import org.dama.datasynth.program.schnappi.ast.Number;
import org.dama.datasynth.program.schnappi.ast.Operation;
import org.dama.datasynth.program.solvers.Solver;

/**
 * Created by quim on 5/17/16.
 */
public class SchnappiGeneratorVisitor extends org.dama.datasynth.program.schnappi.SchnappiParserBaseVisitor<Node> {

    @Override
    public Solver visitSolver(org.dama.datasynth.program.schnappi.SchnappiParser.SolverContext ctx){


        Signature signature = visitSignature(ctx.signature());
        Ast ast = new Ast();
        for(org.dama.datasynth.program.schnappi.SchnappiParser.OpContext op :  ctx.program().op()) {
           ast.addStatement(visitOp(op));

        }
        return new Solver(signature,ast);
    }

    @Override
    public Signature visitSignature(org.dama.datasynth.program.schnappi.SchnappiParser.SignatureContext ctx){
        return new Signature(ctx.source().VTYPE().getText(), ctx.target().VTYPE().getText());
    }

    @Override
    public Operation visitOp(org.dama.datasynth.program.schnappi.SchnappiParser.OpContext ctx){
        if(ctx.assig() != null)  return visitAssig(ctx.assig());
        return null;
    }

    @Override
    public Function visitInit(org.dama.datasynth.program.schnappi.SchnappiParser.InitContext ctx){
        Parameters parameters = new Parameters();
        Any any = null;
        if(ctx.BINDING() != null) any = new Binding(ctx.BINDING().getText());
        if(ctx.ID() != null) any = new StringLiteral(ctx.ID().getText());
        parameters.addParam(any);
        parameters.mergeParams(visitParams(ctx.params()));
        Function function= new Function("init", parameters);
        return function;
    }

    @Override
    public Assign visitAssig(org.dama.datasynth.program.schnappi.SchnappiParser.AssigContext ctx) {

        Any any = null;
        if(ctx.BINDING() != null) any = new Binding(ctx.BINDING().getText());
        if(ctx.ID() != null) any = new Id(ctx.ID().getText());
        return new Assign(any, visitExpr(ctx.expr()));
    }

    @Override
    public Expression visitExpr(org.dama.datasynth.program.schnappi.SchnappiParser.ExprContext ctx){
        if(ctx.funcs() != null) {
            return visitFuncs(ctx.funcs());
        } else if(ctx.any() != null) {
            return visitAny(ctx.any());
        }
        return null;
    }

    @Override
    public Any visitAny(org.dama.datasynth.program.schnappi.SchnappiParser.AnyContext ctx) {
        if(ctx.NUM() != null)  return new Number(ctx.getText());
        if(ctx.BINDING() != null)  return new Binding(ctx.getText());
        if(ctx.STRING() != null)  return new StringLiteral(ctx.getText());
        if(ctx.ID() != null)  return new Id(ctx.getText());
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
        for(org.dama.datasynth.program.schnappi.SchnappiParser.AnyContext tn : ctx.any()) {
                parameters.addParam(visitAny(tn));
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
        for(org.dama.datasynth.program.schnappi.SchnappiParser.AnyContext tn : ctx.any()) {
                parameters.addParam(visitAny(tn));
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
        for( org.dama.datasynth.program.schnappi.SchnappiParser.AnyContext tn : ctx.any()){
            Any any = null;
            if(tn.NUM() != null) any = new Number(tn.getText());
            if(tn.STRING() != null) any = new StringLiteral(tn.getText());
            if(tn.BINDING() != null) any = new Binding(tn.getText());
            parameters.addParam(any);
        }
        return parameters;
    }

    @Override
    public Function visitGenids(org.dama.datasynth.program.schnappi.SchnappiParser.GenidsContext ctx){
        Parameters parameters = new Parameters();
        parameters.addParam(new Literal(ctx.NUM().getSymbol().getText()));
        return new Function("geinds",parameters);
    }
    @Override
    public Function visitSort(org.dama.datasynth.program.schnappi.SchnappiParser.SortContext ctx){
        return new Function("sort",(visitParams(ctx.params())));
    }
    @Override
    public Function visitPartition(org.dama.datasynth.program.schnappi.SchnappiParser.PartitionContext ctx){
        return new Function("partition",(visitParams(ctx.params())));
    }
    @Override
    public Function visitMappart(org.dama.datasynth.program.schnappi.SchnappiParser.MappartContext ctx){
        Parameters parameters = new Parameters();
        for(org.dama.datasynth.program.schnappi.SchnappiParser.AnyContext tn : ctx.any()) {
            parameters.addParam(visitAny(tn));
        }
        return new Function("mappart",parameters);
    }
}