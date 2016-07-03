package org.dama.datasynth.program.solvers;

import org.dama.datasynth.exec.AttributeTask;
import org.dama.datasynth.exec.DEdge;
import org.dama.datasynth.exec.Vertex;
import org.dama.datasynth.program.Ast;
import org.dama.datasynth.program.Parser;
import org.dama.datasynth.program.schnappi.ast.*;
import org.dama.datasynth.utils.Reader;

import java.util.Map;

/**
 * Created by quim on 5/5/16.
 */
public class Solver {
    private String rawRep;
    private Ast ast;
    public Map<String,String> bindings;
    public Signature signature;
    public Solver(Ast astrep) {
        this.ast = astrep;
    }
    private void parse(String file){
        //this.ast = new Parser(file).getAst();
    }
    public Signature getSignature(){
        return this.signature;
    }

    public Ast getAst() {
        return ast;
    }

    public void setAst(Ast ast) {
        this.ast = ast;
    }
    public void instantiate(Node root, DEdge e){
        if(root instanceof AtomNode){
            AtomNode atom = (AtomNode) root;
            if(atom.type == "ID" && bindings.get(atom.id) != null){
                atom.id = bindings.get(atom.id);
            }
        }else if(root instanceof ParamsNode){
            ParamsNode pn = (ParamsNode) root;
            for(int i = 0; i<pn.params.size(); ++i){
                String str = bindings.get(pn.params.get(i));
                if(str != null) {
                    pn.params.set(i,str);
                }
            }
        }else if(root instanceof FuncNode){
            FuncNode fn = (FuncNode) root;
            AttributeTask at = (AttributeTask) e.getSource();
            if(fn.type.equalsIgnoreCase("init")){
                ParamsNode pn = new ParamsNode("params");
                for(String str : at.getInitParameters()) pn.addParam(str);
                fn.addChild(pn);
            }else {
                ParamsNode pn = new ParamsNode("params");
                pn.addParam(String.valueOf(at.getRunParameters().size()));
                fn.addChild(pn);
            }
        }
        for(Node child : root.children){
            instantiate(child, e);
        }
    }
    public Ast instantiate(DEdge e){
        //CopyVisitor cv = new CopyVisitor();
        Ast binded = new Ast(ast.getRoot().copy());
        this.instantiate(binded.getRoot(), e);
        return binded;
    }
}
