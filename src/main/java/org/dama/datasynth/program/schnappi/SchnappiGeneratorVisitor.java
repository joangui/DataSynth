package org.dama.datasynth.program.schnappi;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.dama.datasynth.program.schnappi.ast.Node;
import org.dama.datasynth.program.schnappi.ast.OpNode;

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
        org.dama.datasynth.program.schnappi.SchnappiParser.MapContext mctx = ctx.map();
        org.dama.datasynth.program.schnappi.SchnappiParser.ReduceContext rctx = ctx.reduce();
        if(rctx == null) return visitMap(mctx);
        if(mctx == null) return visitReduce(rctx);
        return null;
    }
    @Override
    public OpNode visitMap(org.dama.datasynth.program.schnappi.SchnappiParser.MapContext ctx) {
        OpNode n = new OpNode("map");
        for(TerminalNode tn : ctx.ID()) {
            n.ids.add(tn.getSymbol().getText());
        }
        return n;
    }
    @Override
    public OpNode visitReduce(org.dama.datasynth.program.schnappi.SchnappiParser.ReduceContext ctx) {
        OpNode n = new OpNode("reduce");
        for(TerminalNode tn : ctx.ID()) {
            n.ids.add(tn.getSymbol().getText());
        }
        return n;
    }
}
