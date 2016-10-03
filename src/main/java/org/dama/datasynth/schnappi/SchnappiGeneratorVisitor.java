package org.dama.datasynth.schnappi;


import org.antlr.v4.runtime.tree.TerminalNode;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.schnappi.ast.Ast;
import org.dama.datasynth.schnappi.ast.*;
import org.dama.datasynth.schnappi.ast.Number;
import org.dama.datasynth.schnappi.ast.Operation;
import org.dama.datasynth.schnappi.solver.Solver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 5/17/16.
 * Visitor implementing the ANTLR visitor that generates the Ast of the solvers
 */
public class SchnappiGeneratorVisitor extends org.dama.datasynth.schnappi.SchnappiParserBaseVisitor<Node> {


    @Override
    public Solver visitSolver(org.dama.datasynth.schnappi.SchnappiParser.SolverContext ctx){


        Signature signature = visitSignature(ctx.signature());
        Ast ast = new Ast();
        for(org.dama.datasynth.schnappi.SchnappiParser.OpContext op :  ctx.program().op()) {
           ast.addOperation(visitOp(op));

        }
        return new Solver(signature,ast);
    }

    @Override
    public Signature visitSignature(org.dama.datasynth.schnappi.SchnappiParser.SignatureContext ctx){
        Signature signature = new Signature();
        signature.addBinding(ctx.source().ID().getText(), ctx.source().VTYPE().getText());
        for(org.dama.datasynth.schnappi.SchnappiParser.SignatureoperationContext operationCtx : ctx.signatureoperation()) {
            signature.addOperation(visitAtomic(operationCtx.atomic(0)),visitAtomic(operationCtx.atomic(1)), Signature.LogicOperator.fromString(operationCtx.logicoperation().getText()));
        }
        return signature;
    }

    @Override
    public Operation visitOp(org.dama.datasynth.schnappi.SchnappiParser.OpContext ctx){
        if(ctx.assig() != null)  return visitAssig(ctx.assig());
        return null;
    }

    @Override
    public Function visitInit(org.dama.datasynth.schnappi.SchnappiParser.InitContext ctx){
        Parameters parameters = visitParams(ctx.params());
        Function function= new Function("init", parameters);
        return function;
    }

    @Override
    public Assign visitAssig(org.dama.datasynth.schnappi.SchnappiParser.AssigContext ctx) {

        Atomic atomic = null;
        if(ctx.binding() != null) {
            atomic = visitBinding(ctx.binding());
        }
        if(ctx.var() != null) atomic = new Var(ctx.var().getText());
        return new Assign(atomic, visitExpr(ctx.expr()));
    }

    @Override
    public Expression visitExpr(org.dama.datasynth.schnappi.SchnappiParser.ExprContext ctx){
        if(ctx.funcs() != null) {
            return visitFuncs(ctx.funcs());
        } else if(ctx.atomic() != null) {
            return visitAtomic(ctx.atomic());
        }
        return null;
    }

    @Override
    public Number visitNum(org.dama.datasynth.schnappi.SchnappiParser.NumContext ctx) {
        if(ctx.INTEGER() != null) return new Number(ctx.INTEGER().getText(), Types.DataType.LONG);
        if(ctx.FLOATING() != null) return new Number(ctx.INTEGER().getText(), Types.DataType.DOUBLE);
        return null;
    }

    @Override
    public Atomic visitAtomic(org.dama.datasynth.schnappi.SchnappiParser.AtomicContext ctx) {
        if(ctx.num() != null)  return visitNum(ctx.num());
        if(ctx.binding() != null) return visitBinding(ctx.binding());
        if(ctx.STRING() != null)  return new StringLiteral(ctx.getText().replace("\'",""));
        if(ctx.var() != null)  return new Var(ctx.getText(), ctx.var().SID() != null);
        return null;
    }


    @Override
    public Expression visitFuncs(org.dama.datasynth.schnappi.SchnappiParser.FuncsContext ctx){
        if(ctx.init() != null) return visitInit(ctx.init());
        else if(ctx.genids() != null) return visitGenids(ctx.genids());
        else if(ctx.map() != null) return visitMap(ctx.map());
        else if(ctx.reduce() != null) return visitReduce(ctx.reduce());
        else if(ctx.union() != null) return visitUnion(ctx.union());
        else if(ctx.eqjoin() != null) return visitEqjoin(ctx.eqjoin());
        else if(ctx.sort() != null) return visitSort(ctx.sort());
        else if(ctx.partition() != null) return visitPartition(ctx.partition());
        else if(ctx.filter() != null) return visitFilter(ctx.filter());
        else if(ctx.mappart() != null) return visitMappart(ctx.mappart());
        return null;
    }

    @Override
    public Function visitMap(org.dama.datasynth.schnappi.SchnappiParser.MapContext ctx) {
        Parameters parameters = new Parameters();
        for(org.dama.datasynth.schnappi.SchnappiParser.AtomicContext tn : ctx.atomic()) {
                parameters.addParam(visitAtomic(tn));
        }
        return new Function("map",parameters);
    }

    @Override
    public Function visitUnion(org.dama.datasynth.schnappi.SchnappiParser.UnionContext ctx){
        return new Function("union",(visitParams(ctx.params())));
    }

    @Override
    public Function visitReduce(org.dama.datasynth.schnappi.SchnappiParser.ReduceContext ctx) {
        Parameters parameters = new Parameters();
        for(org.dama.datasynth.schnappi.SchnappiParser.AtomicContext tn : ctx.atomic()) {
                parameters.addParam(visitAtomic(tn));
        }
        return new Function("reduce",parameters);
    }

    @Override
    public Function visitEqjoin(org.dama.datasynth.schnappi.SchnappiParser.EqjoinContext ctx){
        return new Function("eqjoin",visitParams(ctx.params()));
    }

    @Override
    public Binding visitBinding(org.dama.datasynth.schnappi.SchnappiParser.BindingContext binding) {
        List<String> bindingChain = new ArrayList<String>();
        for(TerminalNode node : binding.ID()) {
            bindingChain.add(node.getText());
        }
        return new Binding(bindingChain);
    }

    @Override
    public Parameters visitParams(org.dama.datasynth.schnappi.SchnappiParser.ParamsContext ctx){
        Parameters parameters = new Parameters();
        for( org.dama.datasynth.schnappi.SchnappiParser.AtomicContext tn : ctx.atomic()){
            Atomic atomic = null;
            if(tn.num() != null) atomic = visitNum(tn.num());
            if(tn.STRING() != null) atomic = new StringLiteral(tn.getText().replace("\'",""));
            if(tn.binding() != null) {
                atomic = visitBinding(tn.binding());
            }
            if(tn.var() != null) atomic = new Var(tn.getText());
            parameters.addParam(atomic);
        }
        return parameters;
    }

    @Override
    public Function visitGenids(org.dama.datasynth.schnappi.SchnappiParser.GenidsContext ctx){
        Parameters parameters = new Parameters();
        parameters.addParam(new Number(ctx.INTEGER().getSymbol().getText(), Types.DataType.LONG));
        return new Function("genids",parameters);
    }

    @Override
    public Function visitSort(org.dama.datasynth.schnappi.SchnappiParser.SortContext ctx){
        Parameters params = new Parameters();
        params.addParam(ctx.ID() != null ? new Var(ctx.ID().getText()) : visitBinding(ctx.binding()));
        for( Expression expr : visitParams(ctx.params()).getParams()) {
            params.addParam(expr);
        }
        return new Function("sort",params);
    }

    @Override
    public Function visitPartition(org.dama.datasynth.schnappi.SchnappiParser.PartitionContext ctx){
        return new Function("partition",(visitParams(ctx.params())));
    }

    @Override
    public Function visitMappart(org.dama.datasynth.schnappi.SchnappiParser.MappartContext ctx){
        Parameters parameters = new Parameters();
        for(org.dama.datasynth.schnappi.SchnappiParser.AtomicContext tn : ctx.atomic()) {
            parameters.addParam(visitAtomic(tn));
        }
        return new Function("mappart",parameters);
    }

    @Override
    public Function visitFilter(org.dama.datasynth.schnappi.SchnappiParser.FilterContext ctx){
        Parameters parameters = new Parameters();
        parameters.addParam(visitAtomic(ctx.atomic()));
        Parameters indices = visitSet(ctx.set());
        parameters.mergeParams(indices);
        return new Function("filter",parameters);
    }

    @Override
    public Parameters visitSet(org.dama.datasynth.schnappi.SchnappiParser.SetContext ctx){
        Parameters params = new Parameters();
        for(TerminalNode tn : ctx.INTEGER()) {
            params.addParam(new Number(tn.getText(), Types.DataType.LONG));
        }
        return params;
    }
}