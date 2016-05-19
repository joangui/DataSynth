package org.dama.datasynth.program.schnappi;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.dama.datasynth.program.schnappi.ast.*;

/**
 * Created by quim on 5/17/16.
 */
public class SchnappiGeneratorVisitor extends org.dama.datasynth.program.schnappi.SchnappiParserBaseVisitor<Node> {
    @Override
    public Node visitProgram(org.dama.datasynth.program.schnappi.SchnappiParser.ProgramContext ctx){
        Node n = new Node("Program");
        System.out.println(ctx.getText());
        for(org.dama.datasynth.program.schnappi.SchnappiParser.OpContext opc : ctx.op()) {
            n.addChild(visitOp(opc));
        }
        return n;
    }
    @Override
    public Node visitOp(org.dama.datasynth.program.schnappi.SchnappiParser.OpContext ctx){
        Node n = new Node("OP");
        if(ctx.assig() != null) n.addChild(visitAssig(ctx.assig()));
        else if(ctx.init() != null) n.addChild(visitInit(ctx.init()));
        return n;
    }
    @Override
    public Node visitInit(org.dama.datasynth.program.schnappi.SchnappiParser.InitContext ctx){
        Node n = new Node("INIT");
        n.addChild(new AtomNode(ctx.ID().getSymbol().getText(), "ID"));
        return n;
    }
    @Override
    public Node visitAssig(org.dama.datasynth.program.schnappi.SchnappiParser.AssigContext ctx) {
        Node n = new Node("ASSIG");
        n.addChild(new AtomNode(ctx.ID().getSymbol().getText(), "ID"));
        n.addChild(visitExpr(ctx.expr()));
        return n;
    }
    @Override
    public Node visitExpr(org.dama.datasynth.program.schnappi.SchnappiParser.ExprContext ctx){
        Node n = new Node("EXPR");
        n.addChild(visitAtom(ctx.atom()));
        return n;
    }
    @Override
    public Node visitAtom(org.dama.datasynth.program.schnappi.SchnappiParser.AtomContext ctx){
        TerminalNode num = ctx.NUM();
        TerminalNode id = ctx.ID();
        org.dama.datasynth.program.schnappi.SchnappiParser.FuncsContext functx = ctx.funcs();
        if(id != null) return new AtomNode(id.getSymbol().getText(), "id");
        else if(num != null) return new AtomNode(num.getSymbol().getText(), "num");
        else return visitFuncs(functx);
    }
    @Override
    public FuncNode visitMap(org.dama.datasynth.program.schnappi.SchnappiParser.MapContext ctx) {
        FuncNode n = new FuncNode("map");
        ParamsNode pn = new ParamsNode("params");
        for(TerminalNode tn : ctx.ID()) {
            pn.addParam(tn.getSymbol().getText());
        }
        n.addParams(pn);
        return n;
    }
    @Override
    public FuncNode visitUnion(org.dama.datasynth.program.schnappi.SchnappiParser.UnionContext ctx){
        FuncNode n = new FuncNode("union");
        n.addParams(visitParams(ctx.params()));
        return n;
    }
    @Override
    public FuncNode visitReduce(org.dama.datasynth.program.schnappi.SchnappiParser.ReduceContext ctx) {
        FuncNode n = new FuncNode("reduce");
        ParamsNode pn = new ParamsNode("params");
        for(TerminalNode tn : ctx.ID()) {
            pn.addParam(tn.getSymbol().getText());
        }
        n.addParams(pn);
        return n;
    }
    @Override
    public FuncNode visitEqjoin(org.dama.datasynth.program.schnappi.SchnappiParser.EqjoinContext ctx){
        FuncNode n = new FuncNode("eqjoin");
        n.addParams(visitParams(ctx.params()));
        return n;
    }
    @Override
    public ParamsNode visitParams(org.dama.datasynth.program.schnappi.SchnappiParser.ParamsContext ctx){
        ParamsNode n = new ParamsNode("params");
        for(TerminalNode tn : ctx.ID()){
            n.addParam(tn.getSymbol().getText());
        }
        return n;
    }
    @Override
    public FuncNode visitGenids(org.dama.datasynth.program.schnappi.SchnappiParser.GenidsContext ctx){
        FuncNode n = new FuncNode("genids");
        ParamsNode pn = new ParamsNode("params");
        pn.addParam(ctx.NUM().getSymbol().getText());
        n.addParams(pn);
        return n;
    }
}
