package org.dama.datasynth.program.solvers;

import org.apache.commons.io.FileUtils;
import org.dama.datasynth.program.Parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by quim on 5/24/16.
 */
public final class Loader {
    public static ArrayList<Solver> loadSolvers(String dir){
        ArrayList<Solver> solvers = new ArrayList<>();
        Iterator it = FileUtils.iterateFiles(new File(dir), null, false);
        while(it.hasNext()){
            solvers.add(new DefaultSolver(((File)it.next()).getPath()));
        }
        return solvers;
    }
}
