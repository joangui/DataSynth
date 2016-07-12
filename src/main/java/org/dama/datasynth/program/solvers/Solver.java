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
    public SignatureVertex signature;
    public Solver(Ast astrep) {
        this.ast = astrep;
    }
    private void parse(String file){
        //this.ast = new Parser(file).getAst();
    }
    public SignatureVertex getSignature(){
        return this.signature;
    }

    public Ast getAst() {
        return ast;
    }

    public void setAst(Ast ast) {
        this.ast = ast;
    }
    public void instantiate(Node root, Vertex v){
        if(root instanceof AtomNode){
            AtomNode atom = (AtomNode) root;
            if(atom.type == "ID" && fetchValue(atom.id, v) != null){
                atom.id = fetchValue(atom.id, v);
            }
        }else if(root instanceof ParamsNode){
            ParamsNode pn = (ParamsNode) root;
            for(int i = 0; i<pn.params.size(); ++i){
                String str = fetchValue(pn.params.get(i), v);
                //str = pn.params.get(i) + ":" + i + ":" + ((AttributeTask) v).getGenerator();
                if(str != null) {
                    pn.params.set(i, str);
                }
            }
        }else if(root instanceof FuncNode){
            FuncNode fn = (FuncNode) root;
            AttributeTask at = (AttributeTask) v;
            if(fn.id.equalsIgnoreCase("init")){
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
            instantiate(child, v);
        }
    }
    private String fetchValue(String id, Vertex v){
        String aux = bindings.get(id);
        if(aux == null) return null;
        if(aux.substring(0,1).equalsIgnoreCase("@")){
            switch(aux){
                case "@source.generator" : {
                    AttributeTask at = (AttributeTask) v;
                    /*System.out.println(" ID " + id + " Generator " + at.getGenerator());*/
                    return at.getGenerator();
                }
                case "@source.input" : {
                    AttributeTask at = (AttributeTask) v;
                    return at.getId()+".input";
                }
                case "@source.id" : {
                    AttributeTask at = (AttributeTask) v;
                    return at.getId();
                }
                default : {
                    return null;
                }
            }
        }else return aux;
    }
    public Ast instantiate(Vertex v){
        //CopyVisitor cv = new CopyVisitor();
        Ast binded = new Ast(ast.getRoot().copy());
        this.instantiate(binded.getRoot(), v);
        return binded;
    }
}
