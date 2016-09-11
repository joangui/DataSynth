package org.dama.datasynth.schnappi;


import org.antlr.v4.runtime.tree.TerminalNode;
import org.dama.datasynth.schnappi.ast.Ast;
import org.dama.datasynth.schnappi.ast.*;
import org.dama.datasynth.schnappi.ast.Number;
import org.dama.datasynth.schnappi.ast.Operation;
import org.dama.datasynth.schnappi.solver.Solver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 5/17/16.
 */
public class SchnappiGeneratorVisitor extends org.dama.datasynth.schnappi.SchnappiParserBaseVisitor<Node> {

    @Override
    public Solver visitSolver(org.dama.datasynth.schnappi.SchnappiParser.SolverContext ctx){


        Signature signature = visitSignature(ctx.signature());
        Ast ast = new Ast();
        for(org.dama.datasynth.schnappi.SchnappiParser.OpContext op :  ctx.program().op()) {
           ast.addStatement(visitOp(op));

        }
        return new Solver(signature,ast);
    }

    @Override
    public Signature visitSignature(org.dama.datasynth.schnappi.SchnappiParser.SignatureContext ctx){
        if(ctx != null) return new Signature(ctx.source().VTYPE().getText(), ctx.target().VTYPE().getText());
        return null;
    }

    @Override
    public Operation visitOp(org.dama.datasynth.schnappi.SchnappiParser.OpContext ctx){
        if(ctx.assig() != null)  return visitAssig(ctx.assig());
        return null;
    }

    @Override
    public Function visitInit(org.dama.datasynth.schnappi.SchnappiParser.InitContext ctx){
        Parameters parameters = new Parameters();
        Expression expr = null;
        if(ctx.binding() != null) {
            List<String> bindingChain = new ArrayList<String>();
            for(TerminalNode node : ctx.binding().ID()) {
                bindingChain.add(node.getText());
            }
            expr = new Binding(bindingChain);
        }
        if(ctx.ID() != null) expr = new StringLiteral(ctx.ID().getText());
        parameters.addParam(expr);
        parameters.mergeParams(visitParams(ctx.params()));
        Function function= new Function("init", parameters);
        return function;
    }

    @Override
    public Assign visitAssig(org.dama.datasynth.schnappi.SchnappiParser.AssigContext ctx) {

        Atomic atomic = null;
        if(ctx.binding() != null) {
            List<String> bindingChain = new ArrayList<String>();
            for(TerminalNode node : ctx.binding().ID()) {
                bindingChain.add(node.getText());
            }
            atomic = new Binding(bindingChain);
        }
        if(ctx.ID() != null) atomic = new Id(ctx.ID().getText());
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
    public Atomic visitAtomic(org.dama.datasynth.schnappi.SchnappiParser.AtomicContext ctx) {
        if(ctx.NUM() != null)  return new Number(ctx.getText());
        if(ctx.binding() != null){
            List<String> bindingChain = new ArrayList<String>();
            for(TerminalNode node : ctx.binding().ID()) {
                bindingChain.add(node.getText());
            }
            return new Binding(bindingChain);
        }
        if(ctx.STRING() != null)  return new StringLiteral(ctx.getText());
        if(ctx.ID() != null)  return new Id(ctx.getText());
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
    public Parameters visitParams(org.dama.datasynth.schnappi.SchnappiParser.ParamsContext ctx){
        Parameters parameters = new Parameters();
        for( org.dama.datasynth.schnappi.SchnappiParser.AtomicContext tn : ctx.atomic()){
            Atomic atomic = null;
            if(tn.NUM() != null) atomic = new Number(tn.getText());
            if(tn.STRING() != null) atomic = new StringLiteral(tn.getText());
            if(tn.binding() != null) {
                List<String> bindingChain = new ArrayList<String>();
                for(TerminalNode node : tn.binding().ID()) {
                    bindingChain.add(node.getText());
                }
                atomic = new Binding(bindingChain);
            }
            if(tn.ID() != null) atomic = new Id(tn.getText());
            parameters.addParam(atomic);
        }
        return parameters;
    }

    @Override
    public Function visitGenids(org.dama.datasynth.schnappi.SchnappiParser.GenidsContext ctx){
        Parameters parameters = new Parameters();
        parameters.addParam(new Literal(ctx.NUM().getSymbol().getText()));
        return new Function("genids",parameters);
    }

    @Override
    public Function visitSort(org.dama.datasynth.schnappi.SchnappiParser.SortContext ctx){
        return new Function("sort",(visitParams(ctx.params())));
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
        for(TerminalNode tn : ctx.NUM()) {
            params.addParam(new Number(tn.getText()));
        }
        return params;
    }
}