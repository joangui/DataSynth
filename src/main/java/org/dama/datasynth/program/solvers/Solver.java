package org.dama.datasynth.program.solvers;

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
    public void instantiate(Node root){
        if(root instanceof AtomNode){
            AtomNode atom = (AtomNode) root;
            if(atom.type == "ID" && bindings.get(atom.id) != null){
                atom.id = bindings.get(atom.id);
            }
        }else if(root instanceof FuncNode){
            ParamsNode params = ((FuncNode) root).params;
            for(int i = 0; i<params.params.size(); ++i){
                String str = bindings.get(params.params.get(i));
                if(str != null) {
                    params.params.set(i,str);
                }
            }
        }
        for(Node child : root.children){
            instantiate(child);
        }
    }
    public Ast instantiate(){
        //CopyVisitor cv = new CopyVisitor();
        Ast binded = new Ast(ast.getRoot().copy());
        this.instantiate(binded.getRoot());
        return binded;
    }
}
