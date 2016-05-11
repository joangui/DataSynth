package org.dama.datasynth.program.schnappi;

import org.dama.datasynth.exec.ExecutionPlan;
import org.dama.datasynth.exec.Task;
import org.dama.datasynth.program.Ast;
import org.dama.datasynth.program.Solver;

import java.util.ArrayList;

/**
 * Created by quim on 5/5/16.
 */
public class Compiler {
    private ArrayList<Solver> solversDB;
    private Ast program;
    public Compiler(){
        loadSolvers();
    }
    private void loadSolvers(){
        //
    }
    private void synthesizePlan(ExecutionPlan exec){
        for(Task t: exec.getEntryPoints()) {

        }
    }
    /*private void solveEdge(Edge e){
        lookup db, possibly indexed by type of edge;
        result = solversDB.get(type of Edge)
    }*/

}
