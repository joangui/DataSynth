package org.dama.datasynth.program.schnappi;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.dama.datasynth.program.schnappi.ast.AtomNode;
import org.dama.datasynth.program.schnappi.ast.ExprNode;
import org.dama.datasynth.program.schnappi.ast.Node;
import org.dama.datasynth.program.schnappi.ast.FuncNode;

/**
 * Created by quim on 5/17/16.
 */
public class SchnappiGeneratorVisitor extends org.dama.datasynth.program.schnappi.SchnappiParserBaseVisitor<Node> {
    @Override
    public Node visitProgram(org.dama.datasynth.program.schnappi.SchnappiParser.ProgramContext ctx){
        Node n = new Node(ctx.getText());
        for(org.dama.datasynth.program.schnappi.SchnappiParser.OpContext opc : ctx.op()) {
            n.addChild(visitOp(opc));
        }
        return n;
    }
    @Override
    public Node visitOp(org.dama.datasynth.program.schnappi.SchnappiParser.OpContext ctx){
        Node n = new Node(ctx.getText());
        n.addChild(visitAssig(ctx.assig()));
        return n;
    }
    @Override
    public Node visitInit(org.dama.datasynth.program.schnappi.SchnappiParser.InitContext ctx){
        Node n = new Node(ctx.getText());
        n.addChild(new AtomNode(ctx.ID().getSymbol().getText(), "ID"));
        return n;
    }
    @Override
    public Node visitAssig(org.dama.datasynth.program.schnappi.SchnappiParser.AssigContext ctx) {
        Node n = new Node(ctx.getText());
        n.addChild(new AtomNode(ctx.ID().getSymbol().getText(), "ID"));
        n.addChild(visitExpr(ctx.expr()));
        return n;
    }
    @Override
    public ExprNode visitExpr(org.dama.datasynth.program.schnappi.SchnappiParser.ExprContext ctx){
        return null;
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
        for(TerminalNode tn : ctx.ID()) {
            n.ids.add(tn.getSymbol().getText());
        }
        return n;
    }
    @Override
    public FuncNode visitReduce(org.dama.datasynth.program.schnappi.SchnappiParser.ReduceContext ctx) {
        FuncNode n = new FuncNode("reduce");
        for(TerminalNode tn : ctx.ID()) {
            n.ids.add(tn.getSymbol().getText());
        }
        return n;
    }
    @Override
    public FuncNode visitJoin(org.dama.datasynth.program.schnappi.SchnappiParser.JoinContext ctx){
        FuncNode n = new FuncNode("join");
        for(TerminalNode tn : ctx.ID()) {
            n.ids.add(tn.getSymbol().getText());
        }
        return n;
    }
    @Override
    public FuncNode visitGenids(org.dama.datasynth.program.schnappi.SchnappiParser.GenidsContext ctx){
        FuncNode n = new FuncNode("genids");
        n.ids.add(ctx.NUM().getSymbol().getText());
        return n;
    }
}
