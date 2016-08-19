package org.dama.datasynth.program.schnappi;


import org.antlr.v4.runtime.tree.TerminalNode;
import org.dama.datasynth.program.schnappi.ast.*;

/**
 * Created by quim on 5/17/16.
 */
public class SchnappiGeneratorVisitor extends org.dama.datasynth.program.schnappi.SchnappiParserBaseVisitor<Node> {

    @Override
    public Node visitSolver(org.dama.datasynth.program.schnappi.SchnappiParser.SolverContext ctx){
        Node n = new Node("Solver");
        n.addChild(visitSignature(ctx.signature()));
        n.addChild(visitBindings(ctx.bindings()));
        n.addChild(visitProgram(ctx.program()));
        return n;
    }

    @Override
    public SignatureNode visitSignature(org.dama.datasynth.program.schnappi.SchnappiParser.SignatureContext ctx){
        SignatureNode n = new SignatureNode("Signature", "signature");
        n.addChild(visitSource(ctx.source()));
        n.addChild(visitTarget(ctx.target()));
        return n;
    }

    @Override
    public SignatureNode visitSource(org.dama.datasynth.program.schnappi.SchnappiParser.SourceContext ctx){
        return new SignatureNode(ctx.VTYPE().getSymbol().getText(), "source");
    }

    @Override
    public SignatureNode visitTarget(org.dama.datasynth.program.schnappi.SchnappiParser.TargetContext ctx){
        return new SignatureNode(ctx.VTYPE().getSymbol().getText(), "target");
    }

    @Override
    public Node visitBindings(org.dama.datasynth.program.schnappi.SchnappiParser.BindingsContext ctx){
        Node n = new Node("Bindings", "bindings");
        for(org.dama.datasynth.program.schnappi.SchnappiParser.BindContext bctx : ctx.bind()){
            n.addChild(visitBind(bctx));
        }
        return n;
    }

    @Override
    public BindingNode visitBind(org.dama.datasynth.program.schnappi.SchnappiParser.BindContext ctx){
        BindingNode n = new BindingNode("Binding", "binding");
        n.rhs = ctx.bindhead().getText();
        n.lhs = ctx.ID().getSymbol().getText();
        return n;
    }

    @Override
    public Node visitProgram(org.dama.datasynth.program.schnappi.SchnappiParser.ProgramContext ctx){
        Node n = new Node("Program");
        for(org.dama.datasynth.program.schnappi.SchnappiParser.OpContext opc : ctx.op()) {
            n.addChild(visitOp(opc));
        }
        return n;
    }

    @Override
    public Node visitOp(org.dama.datasynth.program.schnappi.SchnappiParser.OpContext ctx){
        Node n = new Node("OP", "op");
        n.addChild(visitAssig(ctx.assig()));
        /*if(ctx.assig() != null) n.addChild(visitAssig(ctx.assig()));
        else if(ctx.init() != null) n.addChild(visitInit(ctx.init()));*/
        return n;
    }

    @Override
    public FuncNode visitInit(org.dama.datasynth.program.schnappi.SchnappiParser.InitContext ctx){
        FuncNode n = new FuncNode("init");
        ParamsNode pn = new ParamsNode("params");
        pn.addParam(ctx.ID().getSymbol().getText());
        pn.mergeParams(visitParams(ctx.params()));
        n.addChild(pn);
        return n;
    }

    @Override
    public Node visitAssig(org.dama.datasynth.program.schnappi.SchnappiParser.AssigContext ctx) {
        Node n = new Node("ASSIG", "assig");
        n.addChild(new AtomNode(ctx.ID().getSymbol().getText(), "ID"));
        n.addChild(visitExpr(ctx.expr()));
        return n;
    }

    @Override
    public Node visitExpr(org.dama.datasynth.program.schnappi.SchnappiParser.ExprContext ctx){
        Node n = new Node("EXPR", "expr");
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
    public Node visitFuncs(org.dama.datasynth.program.schnappi.SchnappiParser.FuncsContext ctx){
        if(ctx.init() != null) return visitInit(ctx.init());
        else if(ctx.genids() != null) return visitGenids(ctx.genids());
        else if(ctx.map() != null) return visitMap(ctx.map());
        else if(ctx.reduce() != null) return visitReduce(ctx.reduce());
        else if(ctx.union() != null) return visitUnion(ctx.union());
        else if(ctx.eqjoin() != null) return visitEqjoin(ctx.eqjoin());
        return null;
    }

    @Override
    public FuncNode visitMap(org.dama.datasynth.program.schnappi.SchnappiParser.MapContext ctx) {
        FuncNode n = new FuncNode("map");
        ParamsNode pn = new ParamsNode("params");
        for(TerminalNode tn : ctx.ID()) {
            pn.addParam(tn.getSymbol().getText());
        }
        n.addChild(pn);
        return n;
    }

    @Override
    public FuncNode visitUnion(org.dama.datasynth.program.schnappi.SchnappiParser.UnionContext ctx){
        FuncNode n = new FuncNode("union");
        n.addChild(visitParams(ctx.params()));
        return n;
    }

    @Override
    public FuncNode visitReduce(org.dama.datasynth.program.schnappi.SchnappiParser.ReduceContext ctx) {
        FuncNode n = new FuncNode("reduce");
        ParamsNode pn = new ParamsNode("params");
        for(TerminalNode tn : ctx.ID()) {
            pn.addParam(tn.getSymbol().getText());
        }
        n.addChild(pn);
        return n;
    }

    @Override
    public FuncNode visitEqjoin(org.dama.datasynth.program.schnappi.SchnappiParser.EqjoinContext ctx){
        FuncNode n = new FuncNode("eqjoin");
        n.addChild(visitParams(ctx.params()));
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
        n.addChild(pn);
        return n;
    }
}