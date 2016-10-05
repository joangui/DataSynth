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
            signature.addOperation(visitSignatureendpoint(operationCtx.signatureendpoint(0)),
                                   visitSignatureendpoint(operationCtx.signatureendpoint(1)),
                                   Signature.LogicOperator.fromString(operationCtx.logicoperation().getText()));
        }
        return signature;
    }

    @Override
    public Expression visitSignatureendpoint(org.dama.datasynth.schnappi.SchnappiParser.SignatureendpointContext ctx) {
        if(ctx.bindingexpression() != null) {
            return visitBindingexpression(ctx.bindingexpression());
        }
        if(ctx.num() != null) return visitNum(ctx.num());
        return visitString(ctx.string());
    }


    @Override
    public Operation visitOp(org.dama.datasynth.schnappi.SchnappiParser.OpContext ctx){
        if(ctx.assig() != null)  return visitAssig(ctx.assig());
        return null;
    }

    @Override
    public Function visitInit(org.dama.datasynth.schnappi.SchnappiParser.InitContext ctx){
        List<Expression> parameters = new ArrayList<Expression>();
        for( org.dama.datasynth.schnappi.SchnappiParser.LiteralorbindingContext literal : ctx.literalorbinding()) {
            parameters.add(visitLiteralorbinding(literal));
        }
        Function function= new Function("init", parameters);
        return function;
    }

    @Override
    public Expression visitLiteralorbinding(org.dama.datasynth.schnappi.SchnappiParser.LiteralorbindingContext ctx) {
        if(ctx.literal() != null) return visitLiteral(ctx.literal());
        return visitBindingexpression(ctx.bindingexpression());
    }

    @Override
    public Literal visitLiteral(org.dama.datasynth.schnappi.SchnappiParser.LiteralContext ctx) {
        if(ctx.string() != null) return visitString(ctx.string());
        return visitNum(ctx.num());
    }

    @Override
    public Assign visitAssig(org.dama.datasynth.schnappi.SchnappiParser.AssigContext ctx) {

        Expression expression = null;
        if(ctx.binding() != null) {
            expression = visitBinding(ctx.binding());
        }
        if(ctx.var() != null) expression = new Var(ctx.var().getText());
        return new Assign(expression, visitExpr(ctx.expr()));
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
        if(ctx.string() != null)  return visitString(ctx.string());
        if(ctx.var() != null)  return new Var(ctx.var().ID().getText());
        if(ctx.sid().SID() != null)  return new Id(ctx.sid().SID().getText(),true);
        return null;
    }


    @Override
    public StringLiteral visitString(org.dama.datasynth.schnappi.SchnappiParser.StringContext ctx){
        return new StringLiteral(ctx.getText().replace("\'",""));
    }

    @Override
    public Expression visitFuncs(org.dama.datasynth.schnappi.SchnappiParser.FuncsContext ctx){
        if(ctx.init() != null) return visitInit(ctx.init());
        else if(ctx.spawn() != null) return visitSpawn(ctx.spawn());
        else if(ctx.map() != null) return visitMap(ctx.map());
        else if(ctx.join() != null) return visitJoin(ctx.join());
        else if(ctx.sort() != null) return visitSort(ctx.sort());
        else if(ctx.mappart() != null) return visitMappart(ctx.mappart());
        else if(ctx.range() != null) return visitRange(ctx.range());
        else if(ctx.zip() != null) return visitZip(ctx.zip());
        return null;
    }

    @Override
    public Function visitRange(org.dama.datasynth.schnappi.SchnappiParser.RangeContext ctx) {
        List<Expression> parameters = new ArrayList<Expression>();
        parameters.add(visitNum(ctx.num()));
        return new Function("range",parameters);
    }

    @Override
    public Function visitZip(org.dama.datasynth.schnappi.SchnappiParser.ZipContext ctx) {
        List<Expression> parameters = new ArrayList<Expression>();
        for(org.dama.datasynth.schnappi.SchnappiParser.TableContext table : ctx.table()) {
            parameters.add(visitTable(table));
        }
        return new Function("zip",parameters);
    }

    @Override
    public Function visitMap(org.dama.datasynth.schnappi.SchnappiParser.MapContext ctx) {
        List<Expression> parameters = new ArrayList<Expression>();
        if(ctx.var() != null) parameters.add(visitVar(ctx.var()));
        if(ctx.string() != null) parameters.add(visitString(ctx.string()));
        parameters.add(visitTable(ctx.table()));
        return new Function("map",parameters);
    }

    @Override
    public Expression visitTable(org.dama.datasynth.schnappi.SchnappiParser.TableContext ctx) {
        if(ctx.bindingexpression() != null) return visitBindingexpression(ctx.bindingexpression());
        return visitVar(ctx.var());
    }

    @Override
    public Function visitJoin(org.dama.datasynth.schnappi.SchnappiParser.JoinContext ctx){
        List<Expression> parameters = new ArrayList<Expression>();
        for(org.dama.datasynth.schnappi.SchnappiParser.TableContext table : ctx.table()) {
            parameters.add(visitTable(table));
        }
        return new Function("join",parameters);
    }

    @Override
    public Binding visitBinding(org.dama.datasynth.schnappi.SchnappiParser.BindingContext ctx) {
        Binding binding = new Binding(ctx.ID().getText(), ctx.leaf().getText());
        for(org.dama.datasynth.schnappi.SchnappiParser.EdgeexpansionContext node : ctx.edgeexpansion()) {
            binding.addExpansion(node.ID().getText(),node.arrow().ARROWOUTGOING() != null ? Types.Direction.OUTGOING : Types.Direction.INGOING);
        }
        return binding;
    }

    @Override
    public BindingExpression visitBindingexpression(org.dama.datasynth.schnappi.SchnappiParser.BindingexpressionContext ctx) {
        if(ctx.binding() != null) return visitBinding(ctx.binding());
        return visitBindingfuncs(ctx.bindingfuncs());
    }

    @Override
    public BindingFunction visitBindingfuncs(org.dama.datasynth.schnappi.SchnappiParser.BindingfuncsContext ctx) {
        return new BindingFunction("length",visitBinding(ctx.length().binding()));
    }

    @Override
    public Var visitVar(org.dama.datasynth.schnappi.SchnappiParser.VarContext ctx) {
        return new Var(ctx.getText());
    }

    @Override
    public Function visitSpawn(org.dama.datasynth.schnappi.SchnappiParser.SpawnContext ctx){
        List<Expression> parameters = new ArrayList<Expression>();
        parameters.add(visitVar(ctx.var()));
        if(ctx.INTEGER() != null)
            parameters.add(new Number(ctx.INTEGER().getText(), Types.DataType.LONG));
        if(ctx.bindingexpression() != null)
            parameters.add(visitBindingexpression(ctx.bindingexpression()));
        return new Function("spawn",parameters);
    }

    @Override
    public Function visitSort(org.dama.datasynth.schnappi.SchnappiParser.SortContext ctx){
        List<Expression> parameters = new ArrayList<Expression>();
        parameters.add(visitTable(ctx.table()));
        parameters.add(visitNum(ctx.num()));
        return new Function("sort",parameters);
    }

    @Override
    public Function visitMappart(org.dama.datasynth.schnappi.SchnappiParser.MappartContext ctx){
        List<Expression> parameters = new ArrayList<Expression>();
        if(ctx.var() != null)
            parameters.add(visitVar(ctx.var()));
        if(ctx.string() != null)
            parameters.add(visitString(ctx.string()));
        parameters.add(visitTable(ctx.table()));
        return new Function("mappart",parameters);
    }

}